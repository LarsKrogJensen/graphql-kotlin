package graphql.execution;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLException;
import graphql.language.Field;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static graphql.introspection.Introspection.*;

public abstract class ExecutionStrategy {
    private static final Logger log = LoggerFactory.getLogger(ExecutionStrategy.class);

    protected ValuesResolver valuesResolver = new ValuesResolver();
    protected FieldCollector fieldCollector = new FieldCollector();

    public abstract CompletionStage<ExecutionResult> execute(ExecutionContext executionContext,
                                                             GraphQLObjectType parentType,
                                                             Object source,
                                                             Map<String, List<Field>> fields);

    CompletionStage<ExecutionResult> resolveField(ExecutionContext executionContext,
                                                  GraphQLObjectType parentType,
                                                  Object source,
                                                  List<Field> fields) {
        GraphQLFieldDefinition fieldDef = getFieldDef(executionContext.getGraphQLSchema(), parentType, fields.get(0));

        Map<String, Object> argumentValues = valuesResolver.getArgumentValues(fieldDef.getArguments(),
                                                                              fields.get(0).getArguments(),
                                                                              executionContext.getVariables());

        DataFetchingEnvironment environment = new DataFetchingEnvironment(
                source,
                argumentValues,
                executionContext.getRoot(),
                fields,
                fieldDef.getType(),
                parentType,
                executionContext.getGraphQLSchema()
        );

        CompletionStage completionStage = fieldDef.getDataFetcher().fetch(environment);
        if (completionStage == null) {
            return completeValue(executionContext, fieldDef.getType(), fields, null);
        }

        return completionStage.exceptionally(e -> {
            executionContext.addError(new ExceptionWhileDataFetching((Throwable) e));
            return null;
        }).thenCompose(resolvedValue -> completeValue(executionContext, fieldDef.getType(), fields, resolvedValue));
    }

    protected CompletionStage<ExecutionResult> completeValue(ExecutionContext executionContext,
                                                             GraphQLType fieldType,
                                                             List<Field> fields,
                                                             Object result) {
        CompletableFuture<ExecutionResult> promise = new CompletableFuture<>();

        if (fieldType instanceof GraphQLNonNull) {
            GraphQLNonNull graphQLNonNull = (GraphQLNonNull) fieldType;
            completeValue(executionContext, graphQLNonNull.getWrappedType(), fields, result)
                    .thenAccept(completed -> {
                        if (completed == null) {
                            promise.completeExceptionally(new GraphQLException("Cannot return null for non-nullable type: " + fields));
                        } else {
                            promise.complete(completed);
                        }
                    })
                    .exceptionally(e -> {
                        promise.completeExceptionally(e);
                        return null;
                    });
            return promise;
        } else if (result == null) {
            promise.complete(null);
            return promise;
        } else if (fieldType instanceof GraphQLList) {
            try {
                return completeValueForList(executionContext, (GraphQLList) fieldType, fields, result);
            } catch (Exception e) {
                promise.completeExceptionally(e);
            }
            return promise;
        } else if (fieldType instanceof GraphQLScalarType) {
            try {
                promise.complete(completeValueForScalar((GraphQLScalarType) fieldType, result));
            } catch (Exception e) {
                promise.completeExceptionally(e);
            }
            return promise;
        } else if (fieldType instanceof GraphQLEnumType) {
            try {
                promise.complete(completeValueForEnum((GraphQLEnumType) fieldType, result));
            } catch (Exception e) {
                promise.completeExceptionally(e);
            }
            return promise;
        }


        GraphQLObjectType resolvedType;
        if (fieldType instanceof GraphQLInterfaceType) {
            resolvedType = resolveType((GraphQLInterfaceType) fieldType, result);
        } else if (fieldType instanceof GraphQLUnionType) {
            resolvedType = resolveType((GraphQLUnionType) fieldType, result);
        } else {
            resolvedType = (GraphQLObjectType) fieldType;
        }

        Map<String, List<Field>> subFields = new LinkedHashMap<>();
        List<String> visitedFragments = new ArrayList<>();
        for (Field field : fields) {
            if (field.getSelectionSet() == null) continue;
            fieldCollector.collectFields(executionContext, resolvedType, field.getSelectionSet(), visitedFragments, subFields);
        }

        // Calling this from the executionContext so that you can shift from the simple execution strategy for mutations
        // back to the desired strategy.

        return executionContext.getExecutionStrategy().execute(executionContext, resolvedType, result, subFields);
    }

    private CompletionStage<ExecutionResult> completeValueForList(ExecutionContext executionContext,
                                                                  GraphQLList fieldType,
                                                                  List<Field> fields,
                                                                  Object result) {
        if (result.getClass().isArray()) {
            result = Arrays.asList((Object[]) result);
        }

        return completeValueForList(executionContext, fieldType, fields, (List<Object>) result);
    }

    protected GraphQLObjectType resolveType(GraphQLInterfaceType graphQLInterfaceType, Object value) {
        GraphQLObjectType result = graphQLInterfaceType.getTypeResolver().getType(value);
        if (result == null) {
            throw new GraphQLException("could not determine type");
        }
        return result;
    }

    protected GraphQLObjectType resolveType(GraphQLUnionType graphQLUnionType, Object value) {
        GraphQLObjectType result = graphQLUnionType.getTypeResolver().getType(value);
        if (result == null) {
            throw new GraphQLException("could not determine type");
        }
        return result;
    }


    protected ExecutionResult completeValueForEnum(GraphQLEnumType enumType, Object result) {
        return new ExecutionResultImpl(enumType.getCoercing().serialize(result), null);
    }

    protected ExecutionResult completeValueForScalar(GraphQLScalarType scalarType, Object result) {
        Object serialized = scalarType.getCoercing().serialize(result);
        //6.6.1 http://facebook.github.io/graphql/#sec-Field-entries
        if (serialized instanceof Double && ((Double) serialized).isNaN()) {
            serialized = null;
        }
        return new ExecutionResultImpl(serialized, null);
    }

    protected CompletionStage<ExecutionResult> completeValueForList(ExecutionContext executionContext,
                                                                    GraphQLList fieldType,
                                                                    List<Field> fields,
                                                                    List<Object> result) {

        List<CompletableFuture> completionPromises = new ArrayList<>();
        List<Object> completedResults = new ArrayList<>();
        for (Object item : result) {
            completionPromises.add((CompletableFuture) completeValue(executionContext, fieldType.getWrappedType(), fields, item).thenAccept(
                    completedValue -> completedResults.add(completedValue != null ? completedValue.getData() : null)));
        }

        CompletableFuture[] completionPromisesArray = new CompletableFuture[completionPromises.size()];
        return CompletableFuture.allOf(completionPromises.toArray(completionPromisesArray)).thenApply(aVoid ->
                                                                                                              new ExecutionResultImpl(
                                                                                                                      completedResults, null));
    }

    protected GraphQLFieldDefinition getFieldDef(GraphQLSchema schema, GraphQLObjectType parentType, Field field) {
        if (schema.getQueryType() == parentType) {
            if (field.getName().equals(INSTANCE.getSchemaMetaFieldDef().getName())) {
                return INSTANCE.getSchemaMetaFieldDef();
            }
            if (field.getName().equals(INSTANCE.getTypeMetaFieldDef().getName())) {
                return INSTANCE.getTypeMetaFieldDef();
            }
        }
        if (field.getName().equals(INSTANCE.getTypeNameMetaFieldDef().getName())) {
            return INSTANCE.getTypeNameMetaFieldDef();
        }

        GraphQLFieldDefinition fieldDefinition = parentType.fieldDefinition(field.getName());
        if (fieldDefinition == null) {
            throw new GraphQLException("unknown field " + field.getName());
        }
        return fieldDefinition;
    }


}
