package graphql.execution.instrumentation.parameters

import graphql.execution.ExecutionContext
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.GraphQLFieldDefinition

/**
 * Parameters sent to [Instrumentation] methods
 */
open class FieldParameters(val executionContext: ExecutionContext,
                           val fieldDef: GraphQLFieldDefinition<*>)
