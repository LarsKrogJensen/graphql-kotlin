package graphql.execution

import graphql.ExecutionResult
import graphql.language.Field
import graphql.schema.GraphQLSubscriptionObjectType
import java.util.concurrent.CompletionStage

interface ISubscriptionExecutionStrategy {
    fun execute(executionContext: ExecutionContext,
                parentType: GraphQLSubscriptionObjectType,
                source: Any,
                fields: Map<String, List<Field>>): CompletionStage<ExecutionResult>
}