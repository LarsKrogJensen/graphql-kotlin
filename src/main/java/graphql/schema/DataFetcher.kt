package graphql.schema


import java.util.concurrent.CompletionStage

interface DataFetcher<T> {

    operator fun get(environment: DataFetchingEnvironment): CompletionStage<T>
}
