package graphql


import java.util.ArrayList

class ExecutionResultImpl : ExecutionResult {

    override val errors = ArrayList<GraphQLError>()
    override var data: Any? = null

    constructor(errors: List<GraphQLError>) {
        this.errors.addAll(errors)
    }

    constructor(data: Any, errors: List<GraphQLError>?) {
        this.data = data

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
