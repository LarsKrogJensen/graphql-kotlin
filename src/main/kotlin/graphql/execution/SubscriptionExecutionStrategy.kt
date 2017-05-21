package graphql.execution

import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.language.Field
import graphql.schema.DataFetchingEnvironmentImpl
import graphql.schema.GraphQLObjectType
import reactor.core.publisher.Flux
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CompletionStage

class SubscriptionExecutionStrategy : AbstractExecutionStrategy() {


    override fun execute(executionContext: ExecutionContext,
                         parentType: GraphQLObjectType,
                         source: Any,
                         fields: Map<String, List<Field>>): CompletionStage<ExecutionResult> {

        val fieldSubscriptions = fields.entries.associate { (fieldName, fieldList) ->
            val subscription = resolveSubscriptionField(executionContext,
                                                        parentType,
                                                        source,
                                                        fieldList)
            fieldName to subscription
        }

        return completedFuture(ExecutionResultImpl(data = fieldSubscriptions))
    }

    private fun resolveSubscriptionField(executionContext: ExecutionContext,
                                         parentType: GraphQLObjectType,
                                         source: Any,
                                         fields: List<Field>): Flux<ExecutionResult> {
        val fieldDef = fieldDef(executionContext.graphQLSchema, parentType, fields[0])

        val argumentValues = valuesResolver.argumentValues(fieldDef.arguments,
                                                           fields[0].arguments,
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

        val dataPublisher = fieldDef.publisher ?: throw NullPointerException("Publisher cannot be null")


        return Flux.create<ExecutionResult> { emitter ->
            dataPublisher(environment).subscribe(
                { result ->
                    completeValue(executionContext, fieldDef.type, fields, result)
                        .whenComplete { data, ex ->
                            if (ex != null)
                                emitter.error(ex)
                            else
                                emitter.next(data)
                        }
                },
                emitter::error,
                emitter::complete
            )
        }
    }
}