package graphql


import java.util.ArrayList

class ExecutionResultImpl : ExecutionResult {
    private var _data: Any? = null

    override fun <T> data(): T = _data as T

    override val errors = ArrayList<GraphQLError>()

    constructor(errors: List<GraphQLError>) {
        this.errors.addAll(errors)
    }

    constructor(data: Any?, errors: List<GraphQLError>? = null) {
        _data = data

        if (errors != null) {
            this.errors.addAll(errors)
        }
    }

    fun addErrors(errors: List<GraphQLError>) {
        this.errors.addAll(errors)
    }

    override fun succeeded(): Boolean {
        return errors.isEmpty()
    }
}
