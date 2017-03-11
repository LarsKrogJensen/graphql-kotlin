package graphql.execution.instrumentation

import graphql.ExecutionResult
import graphql.execution.instrumentation.parameters.DataFetchParameters
import graphql.execution.instrumentation.parameters.ExecutionParameters
import graphql.execution.instrumentation.parameters.FieldFetchParameters
import graphql.execution.instrumentation.parameters.FieldParameters
import graphql.execution.instrumentation.parameters.ValidationParameters
import graphql.language.Document
import graphql.schema.DataFetcher
import graphql.validation.ValidationError

/**
 * Provides the capability to instrument the execution steps of a GraphQL query.

 * For example you might want to track which fields are taking the most time to fetch from the backing database
 * or log what fields are being asked for.
 */
interface Instrumentation {

    /**
     * This is called just before a query is executed and when this step finishes the [InstrumentationContext.onEnd]
     * will be called indicating that the step has finished.

     * @param parameters the parameters to this step
     * *
     * *
     * @return a non null [InstrumentationContext] object that will be called back when the step ends
     */
    fun beginExecution(parameters: ExecutionParameters): InstrumentationContext<ExecutionResult>

    /**
     * This is called just before a query is parsed and when this step finishes the [InstrumentationContext.onEnd]
     * will be called indicating that the step has finished.

     * @param parameters the parameters to this step
     * *
     * *
     * @return a non null [InstrumentationContext] object that will be called back when the step ends
     */
    fun beginParse(parameters: ExecutionParameters): InstrumentationContext<Document>

    /**
     * This is called just before the parsed query Document is validated and when this step finishes the [InstrumentationContext.onEnd]
     * will be called indicating that the step has finished.

     * @param parameters the parameters to this step
     * *
     * *
     * @return a non null [InstrumentationContext] object that will be called back when the step ends
     */
    fun beginValidation(parameters: ValidationParameters): InstrumentationContext<List<ValidationError>>

    /**
     * This is called just before the data fetch is started and when this step finishes the [InstrumentationContext.onEnd]
     * will be called indicating that the step has finished.

     * @param parameters the parameters to this step
     * *
     * *
     * @return a non null [InstrumentationContext] object that will be called back when the step ends
     */
    fun beginDataFetch(parameters: DataFetchParameters): InstrumentationContext<ExecutionResult>

    /**
     * This is called just before a field is resolved and when this step finishes the [InstrumentationContext.onEnd]
     * will be called indicating that the step has finished.

     * @param parameters the parameters to this step
     * *
     * *
     * @return a non null [InstrumentationContext] object that will be called back when the step ends
     */
    fun beginField(parameters: FieldParameters): InstrumentationContext<ExecutionResult>

    /**
     * This is called just before a field [DataFetcher] is invoked and when this step finishes the [InstrumentationContext.onEnd]
     * will be called indicating that the step has finished.

     * @param parameters the parameters to this step
     * *
     * *
     * @return a non null [InstrumentationContext] object that will be called back when the step ends
     */
    fun beginFieldFetch(parameters: FieldFetchParameters): InstrumentationContext<Any>
}
