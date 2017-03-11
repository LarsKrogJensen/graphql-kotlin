package graphql.execution


import graphql.GraphQLError
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.language.FragmentDefinition
import graphql.language.OperationDefinition
import graphql.schema.GraphQLSchema
import java.util.concurrent.CopyOnWriteArrayList

class ExecutionContext(val instrumentation: Instrumentation,
                       val executionId: ExecutionId,
                       val graphQLSchema: GraphQLSchema,
                       val queryStrategy: ExecutionStrategy,
                       val mutationStrategy: ExecutionStrategy,
                       val fragmentsByName: Map<String, FragmentDefinition>,
                       val operationDefinition: OperationDefinition,
                       val variables: Map<String, Any?>,
                       private val root: Any) {
    private val errors = CopyOnWriteArrayList<GraphQLError>()

    fun <T> root(): T {

        return root as T
    }

    fun fragment(name: String): FragmentDefinition? {
        return fragmentsByName[name]
    }

    fun addError(error: GraphQLError) {
        this.errors.add(error)
    }

    fun errors(): List<GraphQLError> {
        return errors
    }
}
