package graphql

interface ExecutionResult {

    fun <T> data(): T

    val errors: List<GraphQLError>

    fun succeeded(): Boolean
}
