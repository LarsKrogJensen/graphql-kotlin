package graphql.schema


import graphql.GraphQLBoolean
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun <T> propertyDataFetcher(propertyName: String) : DataFetcher<T> {
    val fetcher = PropertyDataFetcher<T>(propertyName)
    return fetcher::fetch
}

private class PropertyDataFetcher<T>(private val propertyName: String)  {

    fun fetch(environment: DataFetchingEnvironment): CompletionStage<T> {
        val promise = CompletableFuture<T>()
        val source = environment.source<Any?>() ?: return CompletableFuture.completedFuture(null)

        if (source is Map<*, *>) {
            promise.complete(source[propertyName] as T)
            return promise
        }
        promise.complete(getPropertyViaGetter(source, environment.fieldType) as T)
        return promise
    }


    private fun getPropertyViaGetter(obj: Any, outputType: GraphQLOutputType): Any? {
        try {
            if (isBooleanProperty(outputType)) {
                try {
                    return getPropertyViaGetterUsingPrefix(obj, "is")
                } catch (e: NoSuchMethodException) {
                    return getPropertyViaGetterUsingPrefix(obj, "get")
                }

            } else {
                return getPropertyViaGetterUsingPrefix(obj, "get")
            }
        } catch (e1: NoSuchMethodException) {
            return getPropertyViaFieldAccess(obj)
        }

    }

    @Throws(NoSuchMethodException::class)
    private fun getPropertyViaGetterUsingPrefix(`object`: Any, prefix: String): Any {
        val getterName = prefix + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1)
        try {
            val method = `object`.javaClass.getMethod(getterName)
            return method.invoke(`object`)

        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        }

    }

    private fun isBooleanProperty(outputType: GraphQLOutputType): Boolean {
        if (outputType === GraphQLBoolean) return true
        if (outputType is GraphQLNonNull) {
            return outputType.wrappedType === GraphQLBoolean
        }
        return false
    }

    private fun getPropertyViaFieldAccess(obj: Any): Any? {
        try {
            val field = obj.javaClass.getField(propertyName)
            return field.get(obj)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

    }
}
