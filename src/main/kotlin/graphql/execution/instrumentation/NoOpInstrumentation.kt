package graphql.execution.instrumentation

import graphql.ExecutionResult
import graphql.execution.instrumentation.parameters.*
import graphql.language.Document
import graphql.validation.ValidationError

/**
 * Nothing to see or do here ;)
 */
class NoOpInstrumentation private constructor() : Instrumentation {

    override fun beginExecution(parameters: ExecutionParameters): InstrumentationContext<ExecutionResult> {
        return object : InstrumentationContext<ExecutionResult> {
            override fun onEnd(result: ExecutionResult?) {}

            override fun onEnd(e: Exception) {}
        }
    }

    override fun beginParse(parameters: ExecutionParameters): InstrumentationContext<Document> {
        return object : InstrumentationContext<Document> {
            override fun onEnd(result: Document?) {}

            override fun onEnd(e: Exception) {}
        }
    }

    override fun beginValidation(parameters: ValidationParameters): InstrumentationContext<List<ValidationError>> {
        return object : InstrumentationContext<List<ValidationError>> {
            override fun onEnd(result: List<ValidationError>?) {}

            override fun onEnd(e: Exception) {}
        }
    }

    override fun beginDataFetch(parameters: DataFetchParameters): InstrumentationContext<ExecutionResult> {
        return object : InstrumentationContext<ExecutionResult> {
            override fun onEnd(result: ExecutionResult?) {}

            override fun onEnd(e: Exception) {}
        }
    }

    override fun beginField(parameters: FieldParameters): InstrumentationContext<ExecutionResult> {
        return object : InstrumentationContext<ExecutionResult> {
            override fun onEnd(result: ExecutionResult?) {}

            override fun onEnd(e: Exception) {}
        }
    }

    override fun beginFieldFetch(parameters: FieldFetchParameters): InstrumentationContext<Any> {
        return object : InstrumentationContext<Any> {
            override fun onEnd(result: Any?) {}

            override fun onEnd(e: Exception) {}
        }
    }

    companion object {
        var INSTANCE = NoOpInstrumentation()
    }
}
