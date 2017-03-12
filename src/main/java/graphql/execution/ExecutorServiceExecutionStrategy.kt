package graphql.execution

import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.GraphQLException
import graphql.language.Field
import graphql.schema.GraphQLObjectType

import java.util.LinkedHashMap
import java.util.concurrent.*

/**
 *
 * ExecutorServiceExecutionStrategy uses an [ExecutorService] to parallelize the resolve.

 * Due to the nature of [.execute] implementation, [ExecutorService]
 * MUST have the following 2 characteristics:
 *
 *  * 1. The underlying [java.util.concurrent.ThreadPoolExecutor] MUST have a reasonable `maximumPoolSize`
 *  * 2. The underlying [java.util.concurrent.ThreadPoolExecutor] SHALL NOT use its task queue.
 *

 *
 * Failure to follow 1. and 2. can result in a very large number of threads created or hanging. (deadlock)

 * See `graphql.execution.ExecutorServiceExecutionStrategyTest` for example usage.
 */
class ExecutorServiceExecutionStrategy(val executorService: ExecutorService) : AbstractExecutionStrategy() {

    override fun execute(executionContext: ExecutionContext,
                         parentType: GraphQLObjectType,
                         source: Any,
                         fields: Map<String, List<Field>>): CompletionStage<ExecutionResult> {

        val futures = fields.asSequence()
                .associateBy({ it.key }) {
                    executorService.submit( Callable {
                                            resolveField(executionContext, parentType, source, it.value)
                                        })
                }

        try {
            val promise = CompletableFuture<ExecutionResult>()

            val results = LinkedHashMap<String, Any?>()
            for ((fieldName, future) in futures) {
                future.get().thenAccept({ executionResult ->
                                                            results.put(fieldName, executionResult?.data())
                                                            // Last one to finish completes the promise
                                                            if (results.size == futures.keys.size) {
                                                                promise.complete(ExecutionResultImpl(results, executionContext.errors()))
                                                            }
                                                        })
            }
            return promise
        } catch (e: InterruptedException) {
            throw GraphQLException(e)
        } catch (e: ExecutionException) {
            throw GraphQLException(e)
        }

    }
}
