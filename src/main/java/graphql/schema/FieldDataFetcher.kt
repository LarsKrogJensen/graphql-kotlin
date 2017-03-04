package graphql.schema


import java.lang.reflect.Field
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * Fetches data directly from a field.
 */
class FieldDataFetcher<T>(private val fieldName: String) : DataFetcher<T> {

    override fun get(environment: DataFetchingEnvironment): CompletionStage<T> {
        val promise = CompletableFuture<T>()

        when (environment.source) {
            null         -> promise.complete(null)
            is Map<*, *> -> promise.complete(environment.source[fieldName] as T)
            else         -> promise.complete(fieldValue(environment.source))
        }

        return promise
    }

    /**
     * Uses introspection to get the field value.

     * @param object     The object being acted on.
     * *
     * @param outputType The output type; ignored in this case.
     * *
     * @return An object, or null.
     */
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
