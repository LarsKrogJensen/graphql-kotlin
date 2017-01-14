package graphql.execution;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLException;
import graphql.language.Field;
import graphql.schema.GraphQLObjectType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>ExecutorServiceExecutionStrategy uses an {@link ExecutorService} to parallelize the resolve.</p>
 * 
 * Due to the nature of {@link #execute(ExecutionContext, GraphQLObjectType, Object, Map)} implementation, {@link ExecutorService}
 * MUST have the following 2 characteristics:
 * <ul>
 * <li>1. The underlying {@link java.util.concurrent.ThreadPoolExecutor} MUST have a reasonable {@code maximumPoolSize}
 * <li>2. The underlying {@link java.util.concurrent.ThreadPoolExecutor} SHALL NOT use its task queue.
 * </ul>
 * 
 * <p>Failure to follow 1. and 2. can result in a very large number of threads created or hanging. (deadlock)</p>
 * 
 * See {@code graphql.execution.ExecutorServiceExecutionStrategyTest} for example usage.
 */
public class ExecutorServiceExecutionStrategy extends ExecutionStrategy {

    ExecutorService executorService;

    public ExecutorServiceExecutionStrategy(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public CompletionStage<ExecutionResult> execute(final ExecutionContext executionContext, final GraphQLObjectType parentType, final Object source, final Map<String, List<Field>> fields) {
        if (executorService == null)
            return new SimpleExecutionStrategy().execute(executionContext, parentType, source, fields);

        Map<String, Future<CompletionStage<ExecutionResult>>> futures = new LinkedHashMap<>();
        for (String fieldName : fields.keySet()) {
            final List<Field> fieldList = fields.get(fieldName);
            Callable<CompletionStage<ExecutionResult>> resolveField =
                    () -> resolveField(executionContext, parentType, source, fieldList);
            futures.put(fieldName, executorService.submit(resolveField));
        }
        try {
            CompletableFuture<ExecutionResult> promise = new CompletableFuture<>();

            Map<String, Object> results = new LinkedHashMap<String, Object>();
            for (String fieldName : futures.keySet()) {
                futures.get(fieldName).get().thenAccept(executionResult -> {
                    results.put(fieldName, executionResult != null ? executionResult.getData() : null);
                    // Last one to finish completes the promise
                    if (results.size() == futures.keySet().size()) {
                        promise.complete(new ExecutionResultImpl(results, executionContext.getErrors()));
                    }
                });
            }
            return promise;
        } catch (InterruptedException e) {
            throw new GraphQLException(e);
        } catch (ExecutionException e) {
            throw new GraphQLException(e);
        }
    }
}
