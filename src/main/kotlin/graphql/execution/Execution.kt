package graphql.execution


import graphql.ExecutionResult
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.parameters.DataFetchParameters
import graphql.language.Document
import graphql.language.Field
import graphql.language.OperationDefinition
import graphql.language.OperationDefinition.Operation.*
import graphql.schema.GraphQLSchema
import java.util.*
import java.util.concurrent.CompletionStage

class Execution(queryStrategy: IExecutionStrategy?,
                mutationStrategy: IExecutionStrategy?,
                subscriptionStrategy: IExecutionStrategy?,
                private val instrumentation: Instrumentation) {

    private val fieldCollector = FieldCollector()
    private val queryStrategy: IExecutionStrategy = queryStrategy ?: SimpleExecutionStrategy()
    private val mutationStrategy: IExecutionStrategy = mutationStrategy ?: SimpleExecutionStrategy()
    private val subscriptionStrategy: IExecutionStrategy = subscriptionStrategy ?: SimpleExecutionStrategy()

    fun execute(
            executionId: ExecutionId,
            graphQLSchema: GraphQLSchema,
            root: Any,
            document: Document,
            operationName: String?,
            args: Map<String, Any>
    ): CompletionStage<ExecutionResult> {

        val executionContextBuilder = ExecutionContextBuilder(ValuesResolver(), instrumentation)
        val executionContext = executionContextBuilder.executionId(executionId)
                .build(graphQLSchema, queryStrategy, mutationStrategy, subscriptionStrategy, root, document, operationName, args)
        return executeOperation(executionContext, root, executionContext.operationDefinition)
    }

    private fun operationRootType(
            graphQLSchema: GraphQLSchema,
            operationDefinition: OperationDefinition
    ) = when (operationDefinition.operation) {
        MUTATION     -> graphQLSchema.mutationType!!
        QUERY        -> graphQLSchema.queryType
        SUBSCRIPTION -> graphQLSchema.subscriptionType!!
    }

    private fun executeOperation(
            executionContext: ExecutionContext,
            root: Any,
            operationDefinition: OperationDefinition
    ): CompletionStage<ExecutionResult> {

        val dataFetchCtx = instrumentation.beginDataFetch(DataFetchParameters(executionContext))

        val operationRootType = operationRootType(executionContext.graphQLSchema, operationDefinition)

        val fields = LinkedHashMap<String, MutableList<Field>>()
        fieldCollector.collectFields(executionContext,
                                     operationRootType,
                                     operationDefinition.selectionSet,
                                     mutableListOf(),
                                     fields)

        return executionStrategy(operationDefinition)
                .execute(executionContext, operationRootType, root, fields)
                .whenComplete { executionResult, ex ->
                    if (ex != null)
                        dataFetchCtx.onEnd(ex as Exception)
                    else
                        dataFetchCtx.onEnd(executionResult)
                }
    }

    private fun executionStrategy(operationDefinition: OperationDefinition) = when (operationDefinition.operation) {
        MUTATION     -> mutationStrategy
        SUBSCRIPTION -> subscriptionStrategy
        QUERY        -> queryStrategy
    }
}
