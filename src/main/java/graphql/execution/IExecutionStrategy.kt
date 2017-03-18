package graphql.execution

import graphql.ExecutionResult
import graphql.language.Field
import graphql.schema.GraphQLObjectType
import java.util.concurrent.CompletionStage

interface IExecutionStrategy {
    fun execute(executionContext: ExecutionContext,
                parentType: GraphQLObjectType,
                source: Any,
                fields: Map<String, List<Field>>): CompletionStage<ExecutionResult>
}