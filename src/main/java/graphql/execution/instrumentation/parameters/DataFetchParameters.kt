package graphql.execution.instrumentation.parameters

import graphql.execution.ExecutionContext
import graphql.execution.instrumentation.Instrumentation

/**
 * Parameters sent to [Instrumentation] methods
 */
class DataFetchParameters(val executionContext: ExecutionContext)
