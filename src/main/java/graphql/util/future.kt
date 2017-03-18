package graphql.util

import java.util.concurrent.CompletableFuture

fun <T> succeeded(value: T) = CompletableFuture.completedFuture(value)!!

fun <T> failed(ex: Exception) : CompletableFuture<T> {
    val promise = CompletableFuture<T>()
    promise.completeExceptionally(ex)
    return promise
}
