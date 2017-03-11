package graphql.execution.instrumentation.parameters

import graphql.execution.instrumentation.Instrumentation
import graphql.language.Document

/**
 * Parameters sent to [Instrumentation] methods
 */
class ValidationParameters(query: String,
                           operation: String?,
                           context: Any,
                           arguments: Map<String, Any>,
                           val document: Document) : ExecutionParameters(query, operation, context, arguments)
