package graphql.execution

import java.util.*

/**
 * This opaque identifier is used to identify a unique query execution
 */
data class ExecutionId(private val id: String) {

    companion object {

        /**
         * Create an unique identifier from the given string
         * @return a query execution identifier
         */
        fun generate(): ExecutionId {
            return ExecutionId(UUID.randomUUID().toString())
        }

        /**
         * Create an identifier from the given string
         * @param id the string to wrap
         * *
         * @return a query identifier
         */
        fun from(id: String): ExecutionId {
            return ExecutionId(id)
        }
    }
}
