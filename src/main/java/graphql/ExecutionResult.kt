package graphql

interface ExecutionResult {

    val data: Any?

    val errors: List<GraphQLError>

    fun succeeded(): Boolean
}
