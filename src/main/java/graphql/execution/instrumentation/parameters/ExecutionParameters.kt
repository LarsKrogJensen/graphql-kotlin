package graphql.execution.instrumentation.parameters

import graphql.execution.instrumentation.Instrumentation

/**
 * Parameters sent to [Instrumentation] methods
 */
open class ExecutionParameters(val query: String,
                               val operation: String?,
                               private val context: Any,
                               val arguments: Map<String, Any>) {

    fun <T> context(): T {
        return context as T
    }
}
