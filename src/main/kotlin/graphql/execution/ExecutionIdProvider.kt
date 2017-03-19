package graphql.execution

/**
 * A provider of [ExecutionId]s
 */
interface ExecutionIdProvider {

    /**
     * Allows provision of a unique identifier per query execution.

     * @param query         the query to be executed
     * *
     * @param operationName thr name of the operation
     * *
     * @param context       the context object passed to the query
     * *
     * *
     * @return a non null [ExecutionId]
     */
    fun provide(query: String, operationName: String?, context: Any): ExecutionId
}
