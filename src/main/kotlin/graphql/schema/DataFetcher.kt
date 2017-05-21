package graphql.schema


import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

//typealias DataFetcher<T> = (environment: DataFetchingEnvironment) -> T
typealias DataFetcher<T> = (environment: DataFetchingEnvironment) -> CompletionStage<T>

typealias PublisherFetcher<T> = (environment: DataFetchingEnvironment) -> Publisher<T>

fun <T> staticDataFetcher(value: T) = { _: DataFetchingEnvironment -> CompletableFuture.completedFuture(value)}
