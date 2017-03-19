package graphql.schema


import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun <T> fieldDataFetcher(propertyName: String): DataFetcher<T> {
    val fetcher = FieldDataFetcher<T>(propertyName)
    return fetcher::fetch
}


/**
 * Fetches data directly from a field.
 */
private class FieldDataFetcher<T>(private val fieldName: String) {

    fun fetch(environment: DataFetchingEnvironment): CompletionStage<T> {
        val promise = CompletableFuture<T>()

        val source = environment.source<Any?>()
        when (source) {
            null         -> promise.complete(null)
            is Map<*, *> -> promise.complete(source[fieldName] as T)
            else         -> promise.complete(fieldValue(source))
        }

        return promise
    }

    private fun fieldValue(obj: Any): T? {
        try {
            val field = obj.javaClass.getField(fieldName)
            return field.get(obj) as T
        } catch (e: NoSuchFieldException) {
            return null
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

    }
}
