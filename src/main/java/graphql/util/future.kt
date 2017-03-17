package graphql.util

import java.util.concurrent.CompletableFuture

fun <T> succeeded(value: T) = CompletableFuture.completedFuture(value)
