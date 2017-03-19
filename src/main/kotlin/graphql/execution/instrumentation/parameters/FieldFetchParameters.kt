package graphql.execution.instrumentation.parameters

import graphql.execution.ExecutionContext
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition

/**
 * Parameters sent to [Instrumentation] methods
 */
class FieldFetchParameters(
        getExecutionContext: ExecutionContext,
        fieldDef: GraphQLFieldDefinition<*>,
        val environment: DataFetchingEnvironment
) : FieldParameters(getExecutionContext, fieldDef)
