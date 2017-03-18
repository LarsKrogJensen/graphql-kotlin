package graphql

import graphql.execution.*
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.execution.instrumentation.parameters.ExecutionParameters
import graphql.execution.instrumentation.parameters.ValidationParameters
import graphql.language.Document
import graphql.language.SourceLocation
import graphql.parser.Parser
import graphql.schema.GraphQLSchema
import graphql.validation.Validator
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


class GraphQL private constructor(private val graphQLSchema: GraphQLSchema,
                                  private val queryStrategy: IExecutionStrategy,
                                  private val mutationStrategy: IExecutionStrategy,
                                  private val idProvider: ExecutionIdProvider,
                                  private val instrumentation: Instrumentation) {


    class Builder(private val graphQLSchema: GraphQLSchema) {
        private var queryExecutionStrategy: IExecutionStrategy = SimpleExecutionStrategy()
        private var mutationExecutionStrategy: IExecutionStrategy = SimpleExecutionStrategy()
        private var idProvider = DEFAULT_EXECUTION_ID_PROVIDER
        private var instrumentation: Instrumentation = NoOpInstrumentation.INSTANCE


        fun queryExecutionStrategy(executionStrategy: IExecutionStrategy): Builder {
            this.queryExecutionStrategy = executionStrategy
            return this
        }

        fun mutationExecutionStrategy(executionStrategy: IExecutionStrategy): Builder {
            this.mutationExecutionStrategy = executionStrategy
            return this
        }

        fun instrumentation(instrumentation: Instrumentation): Builder {
            this.instrumentation = instrumentation
            return this
        }

        fun executionIdProvider(executionIdProvider: ExecutionIdProvider): Builder {
            this.idProvider = executionIdProvider
            return this
        }

        fun build(): GraphQL {
            return GraphQL(graphQLSchema, queryExecutionStrategy, mutationExecutionStrategy, idProvider, instrumentation)
        }
    }

    fun execute(requestString: String): CompletionStage<ExecutionResult> {
        return execute(requestString, null)
    }

    fun execute(requestString: String,
                operationName: String? = null,
                context: Any = Any(),
                arguments: Map<String, Any> = emptyMap<String, Any>()): CompletionStage<ExecutionResult> {

        val executionCtx = instrumentation.beginExecution(ExecutionParameters(requestString,
                                                                              operationName,
                                                                              context,
                                                                              arguments))

        log.debug("Executing request. operation name: {}. Request: {} ", operationName, requestString)

        val parseCtx = instrumentation.beginParse(ExecutionParameters(requestString, operationName, context, arguments))
        val parser = Parser()
        val document: Document
        try {
            document = parser.parseDocument(requestString)
            parseCtx.onEnd(document)
        } catch (e: ParseCancellationException) {
            val recognitionException = e.cause as RecognitionException
            val sourceLocation = SourceLocation(recognitionException.offendingToken.line, recognitionException.offendingToken.charPositionInLine)
            val invalidSyntaxError = InvalidSyntaxError(sourceLocation)
            return CompletableFuture.completedFuture(ExecutionResultImpl(listOf(invalidSyntaxError)))
        }

        val validationCtx = instrumentation.beginValidation(ValidationParameters(requestString, operationName, context, arguments, document))

        val validator = Validator()
        val validationErrors = validator.validateDocument(graphQLSchema, document)

        validationCtx.onEnd(validationErrors)

        if (validationErrors.isNotEmpty()) {
            return CompletableFuture.completedFuture(ExecutionResultImpl(validationErrors))
        }
        val executionId = idProvider.provide(requestString, operationName, context)

        val execution = Execution(queryStrategy, mutationStrategy, instrumentation)
        val result = execution.execute(executionId, graphQLSchema, context, document, operationName, arguments)

        result.whenComplete { exeResult, ex ->
            if (ex != null)
                executionCtx.onEnd(ex as Exception)
            else
                executionCtx.onEnd(exeResult)
        }

        return result
    }

    companion object {

        private val log = LoggerFactory.getLogger(GraphQL::class.java)

        private val DEFAULT_EXECUTION_ID_PROVIDER: ExecutionIdProvider = object : ExecutionIdProvider {
            override fun provide(query: String, operationName: String?, context: Any): ExecutionId {
                return ExecutionId.generate()
            }
        }

        @JvmStatic
        fun newGraphQL(graphQLSchema: GraphQLSchema): GraphQL.Builder {
            return GraphQL.Builder(graphQLSchema)
        }
    }
}

fun newGraphQL(graphQLSchema: GraphQLSchema): GraphQL.Builder {
    return GraphQL.Builder(graphQLSchema)
}