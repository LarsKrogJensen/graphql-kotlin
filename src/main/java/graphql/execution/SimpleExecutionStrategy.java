package graphql.execution;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.language.Field;
import graphql.schema.GraphQLObjectType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SimpleExecutionStrategy
    extends ExecutionStrategy
{
    @Override
    public CompletionStage<ExecutionResult> execute(ExecutionContext executionContext,
                                                    GraphQLObjectType parentType,
                                                    Object source,
                                                    Map<String, List<Field>> fields)
    {
        CompletableFuture<ExecutionResult> promise = new CompletableFuture<>();

        List<CompletableFuture> fieldPromises = new ArrayList<>();
        Map<String, Object> results = new LinkedHashMap<String, Object>();
        for (String fieldName : fields.keySet()) {
            List<Field> fieldList = fields.get(fieldName);
            fieldPromises.add((CompletableFuture)resolveField(executionContext, parentType, source, fieldList)
                .thenAccept(resolvedResult -> {
                    results.put(fieldName, resolvedResult != null ? resolvedResult.getData() : null);
                })
                .exceptionally(e -> {
                    promise.completeExceptionally(e);
                    return null;
                }));

        }

        CompletableFuture[] fieldPromisesArray = new CompletableFuture[fieldPromises.size()];
        CompletableFuture.allOf(fieldPromises.toArray(fieldPromisesArray))
                         .thenAccept(aVoid -> promise.complete(new ExecutionResultImpl(results, executionContext.getErrors())));
        return promise;
    }
}
