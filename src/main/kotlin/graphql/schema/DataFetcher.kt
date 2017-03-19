package graphql.schema


import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

typealias DataFetcher<T> = (environment: DataFetchingEnvironment) -> CompletionStage<T>


fun <T> staticDataFetcher(value: T) = { _: DataFetchingEnvironment -> CompletableFuture.completedFuture(value)}
