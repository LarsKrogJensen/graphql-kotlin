package graphql.schema


import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class StaticDataFetcher<T>(private val value: T) : DataFetcher<T> {

    override fun get(environment: DataFetchingEnvironment): CompletionStage<T> {
        return CompletableFuture.completedFuture(value)
    }
}
