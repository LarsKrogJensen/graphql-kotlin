package graphql


import graphql.execution.Execution
import graphql.execution.ExecutionStrategy
import graphql.language.Document
import graphql.language.SourceLocation
import graphql.parser.Parser
import graphql.schema.GraphQLSchema
import graphql.validation.ValidationError
import graphql.validation.Validator
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Collections
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

import graphql.Assert.assertNotNull

class GraphQL(private val graphQLSchema: GraphQLSchema,
              private val executionStrategy: ExecutionStrategy? = null) {


    fun execute(requestString: String,
                operationName: String?,
                context: Any,
                arguments: Map<String, Any> = emptyMap<String, Any>()): CompletionStage<ExecutionResult> {
        val promise = CompletableFuture<ExecutionResult>()

        assertNotNull(arguments, "arguments can't be null")
        log.info("Executing request. operation name: {}. Request: {} ", operationName, requestString)
        val parser = Parser()
        val document: Document
        try {
            document = parser.parseDocument(requestString)
        } catch (e: ParseCancellationException) {
            val recognitionException = e.cause as RecognitionException
            val sourceLocation = SourceLocation(recognitionException.offendingToken.line, recognitionException.offendingToken.charPositionInLine)
            val invalidSyntaxError = InvalidSyntaxError(sourceLocation)
            promise.complete(ExecutionResultImpl(listOf(invalidSyntaxError)))
            return promise
        }

        val validator = Validator()
        val validationErrors = validator.validateDocument(graphQLSchema, document)
        if (validationErrors.isNotEmpty()) {
            promise.complete(ExecutionResultImpl(validationErrors))
            return promise
        }

        val execution = Execution(executionStrategy)
        return execution.execute(graphQLSchema, context, document, operationName, arguments)
    }

    companion object {

        private val log = LoggerFactory.getLogger(GraphQL::class.java)
    }


}
