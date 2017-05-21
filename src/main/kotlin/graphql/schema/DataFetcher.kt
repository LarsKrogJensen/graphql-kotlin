package graphql.schema


import reactor.core.publisher.Flux
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

//typealias DataFetcher<T> = (environment: DataFetchingEnvironment) -> T
typealias DataFetcher<T> = (environment: DataFetchingEnvironment) -> CompletionStage<T>

typealias PublisherFetcher<T> = (environment: DataFetchingEnvironment) -> Flux<T>

fun <T> staticDataFetcher(value: T) = { _: DataFetchingEnvironment -> CompletableFuture.completedFuture(value)}
