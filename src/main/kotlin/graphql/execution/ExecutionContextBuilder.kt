package graphql.execution

import graphql.GraphQLException
import graphql.execution.instrumentation.Instrumentation
import graphql.language.Document
import graphql.language.FragmentDefinition
import graphql.language.OperationDefinition
import graphql.schema.GraphQLSchema

import java.util.LinkedHashMap

import graphql.Assert.assertNotNull

class ExecutionContextBuilder(private val valuesResolver: ValuesResolver,
                              private val instrumentation: Instrumentation) {
    private var executionId: ExecutionId? = null


    fun executionId(executionId: ExecutionId): ExecutionContextBuilder {
        this.executionId = executionId
        return this
    }

    fun build(graphQLSchema: GraphQLSchema,
              queryStrategy: IExecutionStrategy,
              mutationStrategy: IExecutionStrategy,
              root: Any,
              document: Document,
              operationName: String?,
              args: Map<String, Any>): ExecutionContext {
        // preconditions
        assertNotNull(executionId, "You must provide a query identifier")

        val fragmentsByName = LinkedHashMap<String, FragmentDefinition>()
        val operationsByName = LinkedHashMap<String?, OperationDefinition>()

        for (definition in document.definitions) {
            if (definition is OperationDefinition) {
                //definition.name?.let { operationsByName.put(it, definition) }
                operationsByName.put(definition.name, definition)
            }
            if (definition is FragmentDefinition) {
                val fragmentDefinition = definition
                fragmentsByName.put(fragmentDefinition.name, fragmentDefinition)
            }
        }
        if (operationName == null && operationsByName.size > 1) {
            throw GraphQLException("missing operation name")
        }
        val operation: OperationDefinition?

        if (operationName == null) {
            operation = operationsByName.values.iterator().next()
        } else {
            operation = operationsByName[operationName]
        }
        if (operation == null) {
            throw GraphQLException()
        }
        val variableValues = valuesResolver.getVariableValues(graphQLSchema, operation.variableDefinitions, args)

        return ExecutionContext(
                instrumentation,
                executionId!!,
                graphQLSchema,
                queryStrategy,
                mutationStrategy,
                fragmentsByName,
                operation,
                variableValues,
                root)
    }
}
