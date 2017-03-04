package graphql.schema


import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Field
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

import graphql.Scalars.GraphQLBoolean

class PropertyDataFetcher<T>(private val propertyName: String) : DataFetcher<T> {

    override fun get(environment: DataFetchingEnvironment): CompletionStage<T> {
        val promise = CompletableFuture<T>()
        val source = environment.source
        if (source is Map<*, *>) {
            promise.complete(source[propertyName] as T)
            return promise
        }
        promise.complete(getPropertyViaGetter(source, environment.fieldType) as T)
        return promise
    }


    private fun getPropertyViaGetter(obj: Any, outputType: GraphQLOutputType): Any {
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
        if (outputType === INSTANCE.getGraphQLBoolean()) return true
        if (outputType is GraphQLNonNull) {
            return outputType.wrappedType === INSTANCE.getGraphQLBoolean()
        }
        return false
    }

    private fun getPropertyViaFieldAccess(`object`: Any): Any? {
        try {
            val field = `object`.javaClass.getField(propertyName)
            return field.get(`object`)
        } catch (e: NoSuchFieldException) {
            return null
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

    }
}
