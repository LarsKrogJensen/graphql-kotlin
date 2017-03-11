package graphql.execution

import graphql.ExceptionWhileDataFetching
import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.GraphQLException
import graphql.introspection.Introspection.SchemaMetaFieldDef
import graphql.introspection.Introspection.TypeMetaFieldDef
import graphql.introspection.Introspection.TypeNameMetaFieldDef
import graphql.language.Field
import graphql.schema.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

abstract class ExecutionStrategy {

    protected var valuesResolver = ValuesResolver()
    protected var fieldCollector = FieldCollector()

    abstract fun execute(executionContext: ExecutionContext,
                         parentType: GraphQLObjectType,
                         source: Any,
                         fields: Map<String, List<Field>>): CompletionStage<ExecutionResult>

    internal fun resolveField(executionContext: ExecutionContext,
                              parentType: GraphQLObjectType,
                              source: Any,
                              fields: List<Field>): CompletionStage<ExecutionResult> {
        val fieldDef = fieldDef(executionContext.graphQLSchema, parentType, fields[0])

        val argumentValues = valuesResolver.argumentValues(fieldDef.arguments,
                                                           fields.get(0).arguments,
                                                           executionContext.variables)

        val environment = DataFetchingEnvironmentImpl(
                source,
                argumentValues,
                executionContext.root<Any>(),
                fields,
                fieldDef.type,
                parentType,
                executionContext.graphQLSchema
        )

        val completionStage = if (fieldDef.dataFetcher != null) (fieldDef.dataFetcher)(environment) else null
        if (completionStage == null) {
            return completeValue(executionContext, fieldDef.type, fields, null)
        }

        return completionStage.exceptionally { e ->
            executionContext.addError(ExceptionWhileDataFetching(e as Throwable))
            null
        }.thenCompose {
            resolvedValue ->
            completeValue(executionContext, fieldDef.type, fields, resolvedValue)
        }
    }

    protected fun completeValue(executionContext: ExecutionContext,
                                fieldType: GraphQLType,
                                fields: List<Field>,
                                result: Any?): CompletionStage<ExecutionResult> {
        val promise = CompletableFuture<ExecutionResult>()

        if (fieldType is GraphQLNonNull) {
            val graphQLNonNull = fieldType
            completeValue(executionContext, graphQLNonNull.wrappedType, fields, result)
                    .thenAccept({ completed ->
                                    if (completed == null) {
                                        promise.completeExceptionally(GraphQLException("Cannot return null for non-nullable type: " + fields))
                                    } else {
                                        promise.complete(completed)
                                    }
                                })
                    .exceptionally({ e ->
                                       promise.completeExceptionally(e)
                                       null
                                   })
            return promise
        } else if (result == null) {
            promise.complete(null)
            return promise
        } else if (fieldType is GraphQLList) {
            try {
                return completeValueForList(executionContext, fieldType, fields, result)
            } catch (e: Exception) {
                promise.completeExceptionally(e)
            }

            return promise
        } else if (fieldType is GraphQLScalarType) {
            try {
                promise.complete(completeValueForScalar(fieldType, result))
            } catch (e: Exception) {
                promise.completeExceptionally(e)
            }

            return promise
        } else if (fieldType is GraphQLEnumType) {
            try {
                promise.complete(completeValueForEnum(fieldType, result))
            } catch (e: Exception) {
                promise.completeExceptionally(e)
            }

            return promise
        }


        val resolvedType: GraphQLObjectType
        if (fieldType is GraphQLInterfaceType) {
            resolvedType = resolveType(fieldType, result)
        } else if (fieldType is GraphQLUnionType) {
            resolvedType = resolveType(fieldType, result)
        } else {
            resolvedType = fieldType as GraphQLObjectType
        }

        val subFields = LinkedHashMap<String, MutableList<Field>>()
        val visitedFragments = ArrayList<String>()
        for (field in fields) {
            if (!field.selectionSet.isEmpty())
                fieldCollector.collectFields(executionContext,
                                             resolvedType,
                                             field.selectionSet,
                                             visitedFragments,
                                             subFields)
        }

        // Calling this from the executionContext so that you can shift from the simple execution strategy for mutations
        // back to the desired strategy.

        return executionContext.queryStrategy.execute(executionContext, resolvedType, result, subFields)
    }

    private fun completeValueForList(executionContext: ExecutionContext,
                                     fieldType: GraphQLList,
                                     fields: List<Field>,
                                     result: Any): CompletionStage<ExecutionResult> {
        var result1 = result
        if (result1.javaClass.isArray()) {
            result1 = Arrays.asList<Any>(*(result1 as Array<Any>))
        }


        return completeValueForList(executionContext, fieldType, fields, result1 as List<Any>)
    }

    protected fun resolveType(graphQLInterfaceType: GraphQLInterfaceType, value: Any): GraphQLObjectType {
        return graphQLInterfaceType.typeResolver(value) ?: throw GraphQLException("could not determine type")
    }

    protected fun resolveType(graphQLUnionType: GraphQLUnionType, value: Any): GraphQLObjectType {
        return graphQLUnionType.typeResolver(value) ?: throw GraphQLException("could not determine type")
    }


    protected fun completeValueForEnum(enumType: GraphQLEnumType, result: Any): ExecutionResult {
        return ExecutionResultImpl(enumType.coercing.serialize(result), null)
    }

    protected fun completeValueForScalar(scalarType: GraphQLScalarType, result: Any): ExecutionResult {
        var serialized: Any? = scalarType.coercing.serialize(result)
        //6.6.1 http://facebook.github.io/graphql/#sec-Field-entries
        if (serialized is Double && (serialized as Double).isNaN()) {
            serialized = null
        }
        return ExecutionResultImpl(serialized, null)
    }

    protected fun completeValueForList(executionContext: ExecutionContext,
                                       fieldType: GraphQLList,
                                       fields: List<Field>,
                                       result: List<Any>): CompletionStage<ExecutionResult> {

        val completionPromises = ArrayList<CompletableFuture<*>>()


        val completedResults = ArrayList<Any?>()
        for (item in result) {
            val completeValue = completeValue(executionContext, fieldType.wrappedType, fields, item)
            val element = completeValue.thenAccept { completedValue ->
                completedResults.add(completedValue?.data())
            }
            completionPromises.add(element as CompletableFuture<*>)
        }

        return CompletableFuture.allOf(*completionPromises.toTypedArray()).thenApply<ExecutionResult> {
            ExecutionResultImpl(completedResults, null)
        }
    }

    protected fun fieldDef(schema: GraphQLSchema, parentType: GraphQLObjectType, field: Field): GraphQLFieldDefinition<*> {
        if (schema.queryType == parentType) {
            if (field.name == SchemaMetaFieldDef.name) {
                return SchemaMetaFieldDef
            }
            if (field.name == TypeMetaFieldDef.name) {
                return TypeMetaFieldDef;
            }
        }
        if (field.name == TypeNameMetaFieldDef.name) {
            return TypeNameMetaFieldDef;
        }

        return parentType.fieldDefinition(field.name) ?: throw GraphQLException("unknown field " + field.name)
    }
}
