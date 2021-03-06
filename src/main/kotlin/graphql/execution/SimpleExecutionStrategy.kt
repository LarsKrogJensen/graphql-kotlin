package graphql.execution

import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.language.Field
import graphql.schema.GraphQLObjectType
import graphql.util.awaitAll
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

open class SimpleExecutionStrategy : AbstractExecutionStrategy() {
    override fun execute(executionContext: ExecutionContext,
                         parentType: GraphQLObjectType,
                         source: Any,
                         fields: Map<String, List<Field>>): CompletionStage<ExecutionResult> {
        val promise = CompletableFuture<ExecutionResult>()

        //println("**** Executing ${parentType.name} source ${source.javaClass.name} fields ${fields.keys.map { it }}")

        val results = LinkedHashMap<String, Any?>()
        fields.map { (fieldName, fieldList) ->
            resolveField(executionContext, parentType, source, fieldList)
                    .whenComplete { resolvedResult, ex ->
                        if (ex == null) {
                            results.put(fieldName, resolvedResult?.data())
                        } else
                            promise.completeExceptionally(ex)
                    }.toCompletableFuture()

        }.awaitAll().thenAccept {
            promise.complete(ExecutionResultImpl(results, executionContext.errors()))
        }

        return promise
    }
}

