package graphql.execution


import graphql.ExecutionResult
import graphql.GraphQLException
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.parameters.DataFetchParameters
import graphql.language.Document
import graphql.language.Field
import graphql.language.OperationDefinition
import graphql.schema.GraphQLObjectType
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

    fun execute(executionId: ExecutionId,
                graphQLSchema: GraphQLSchema,
                root: Any,
                document: Document,
                operationName: String?,
                args: Map<String, Any>): CompletionStage<ExecutionResult> {

        val executionContextBuilder = ExecutionContextBuilder(ValuesResolver(), instrumentation)
        val executionContext = executionContextBuilder
                .executionId(executionId)
                .build(graphQLSchema, queryStrategy, mutationStrategy, subscriptionStrategy, root, document, operationName, args)
        return executeOperation(executionContext, root, executionContext.operationDefinition)
    }

    private fun operationRootType(graphQLSchema: GraphQLSchema,
                                  operationDefinition: OperationDefinition): GraphQLObjectType {
        if (operationDefinition.operation === OperationDefinition.Operation.MUTATION) {
            return graphQLSchema.mutationType!!

        } else if (operationDefinition.operation === OperationDefinition.Operation.QUERY) {
            return graphQLSchema.queryType

        } else if (operationDefinition.operation === OperationDefinition.Operation.SUBSCRIPTION) {
            return graphQLSchema.subscriptionType!!

        } else {
            throw GraphQLException()
        }
    }

    private fun executeOperation(executionContext: ExecutionContext,
                                 root: Any,
                                 operationDefinition: OperationDefinition): CompletionStage<ExecutionResult> {

        val dataFetchCtx = instrumentation.beginDataFetch(DataFetchParameters(executionContext))

        val operationRootType = operationRootType(executionContext.graphQLSchema, operationDefinition)

        val fields = LinkedHashMap<String, MutableList<Field>>()
        fieldCollector.collectFields(executionContext,
                                     operationRootType,
                                     operationDefinition.selectionSet,
                                     mutableListOf(),
                                     fields)

        val promise = if (operationDefinition.operation === OperationDefinition.Operation.MUTATION) {
            mutationStrategy.execute(executionContext, operationRootType, root, fields)
        } else if (operationDefinition.operation === OperationDefinition.Operation.SUBSCRIPTION) {
            subscriptionStrategy.execute(executionContext, operationRootType, root, fields)
        }  else {
            queryStrategy.execute(executionContext, operationRootType, root, fields);
        }

        promise.whenComplete { executionResult, ex ->
            if (ex != null)
                dataFetchCtx.onEnd(ex as Exception)
            else
                dataFetchCtx.onEnd(executionResult)
        }
        return promise
    }
}
