package graphql

import graphql.execution.*
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.execution.instrumentation.onEnd
import graphql.execution.instrumentation.parameters.ExecutionParameters
import graphql.execution.instrumentation.parameters.ValidationParameters
import graphql.language.Document
import graphql.language.SourceLocation
import graphql.parser.Parser
import graphql.schema.GraphQLSchema
import graphql.util.*
import graphql.validation.Validator
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.properties.Delegates


class GraphQL private constructor(private val graphQLSchema: GraphQLSchema,
                                  private val queryStrategy: IExecutionStrategy,
                                  private val mutationStrategy: IExecutionStrategy,
                                  private val subscriptionStrategy: IExecutionStrategy,
                                  private val idProvider: ExecutionIdProvider,
                                  private val instrumentation: Instrumentation,
                                  private val docCache: IDocumentCache) {


    class Builder {
        var schema: GraphQLSchema by Delegates.notNull<GraphQLSchema>()
        var queryExecutionStrategy: IExecutionStrategy = SimpleExecutionStrategy()
        var mutationExecutionStrategy: IExecutionStrategy = SimpleExecutionStrategy()
        var subscriptionStrategy: IExecutionStrategy = SubscriptionExecutionStrategy()
        var idProvider = DEFAULT_EXECUTION_ID_PROVIDER
        var instrumentation: Instrumentation = NoOpInstrumentation.INSTANCE
        var documentCache: IDocumentCache = NoOpDocumentCache.INSTANCE


        fun queryExecutionStrategy(executionStrategy: IExecutionStrategy): Builder {
            this.queryExecutionStrategy = executionStrategy
            return this
        }

        fun mutationExecutionStrategy(executionStrategy: IExecutionStrategy): Builder {
            this.mutationExecutionStrategy = executionStrategy
            return this
        }

        fun subscriptionExecutionStrategy(executionStrategy: IExecutionStrategy): Builder {
            this.subscriptionStrategy = executionStrategy
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
            return GraphQL(schema,
                           queryExecutionStrategy,
                           mutationExecutionStrategy,
                           subscriptionStrategy,
                           idProvider,
                           instrumentation,
                           documentCache)
        }
    }

    fun execute(requestString: String): CompletionStage<ExecutionResult> {
        return execute(requestString, null)
    }

    fun execute(requestString: String,
                operationName: String? = null,
                context: Any = Any(),
                arguments: Map<String, Any> = emptyMap<String, Any>()): CompletionStage<ExecutionResult> {

        val future = CompletableFuture<ExecutionResult>()

        val parameters = ExecutionParameters(requestString,
                                             operationName,
                                             context,
                                             arguments)
        val executionCtx = instrumentation.beginExecution(parameters)

        docCache.get(requestString).thenApply { (cacheHit, document, errors) ->
            if (cacheHit)
                eitherOf(errors, document)
            else
                parseAndValidate(requestString, operationName, context, arguments)
        }.thenApply { either ->
            try {
                either.fold(
                    left = { errors -> future.complete(ExecutionResultImpl(null, errors)) },
                    right = { document ->
                        val executionId = idProvider.provide(requestString, operationName, context)

                        val execution = Execution(queryStrategy, mutationStrategy, subscriptionStrategy, instrumentation)
                        execution.execute(executionId, graphQLSchema, context, document, operationName, arguments)
                            .whenComplete { exeResult, ex ->
                                executionCtx.onEnd(exeResult, ex)
                                if (ex != null)
                                    future.completeExceptionally(ex)
                                else
                                    future.complete(exeResult)
                            }
                    }
                )
            } catch(e: Exception) {
                future.completeExceptionally(e)
            }
        }

        return future
    }

    private fun parseAndValidate(requestString: String, operationName: String?, context: Any, arguments: Map<String, Any>): Either<List<GraphQLError>, Document> {
        val start = System.currentTimeMillis()
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
            return Left(listOf(invalidSyntaxError))
        }

        val validationCtx = instrumentation.beginValidation(ValidationParameters(requestString, operationName, context, arguments, document))

        val validator = Validator()
        val validationErrors = validator.validateDocument(graphQLSchema, document)

        validationCtx.onEnd(validationErrors)

        if (validationErrors.isNotEmpty()) {
            return Left(validationErrors)
        }
        val end = System.currentTimeMillis()
        log.debug("Parse and validate took ${end - start} ms")

        return Right(document)
    }

    companion object {

        private val log = LoggerFactory.getLogger(GraphQL::class.java)

        private val DEFAULT_EXECUTION_ID_PROVIDER: ExecutionIdProvider = object : ExecutionIdProvider {
            override fun provide(query: String, operationName: String?, context: Any): ExecutionId {
                return ExecutionId.generate()
            }
        }

        @JvmStatic
        fun newGraphQL(schema: GraphQLSchema): Builder {
            val builder = Builder()
            builder.schema = schema
            return builder
        }
    }
}

fun newGraphQL(block: GraphQL.Builder.() -> Unit): GraphQL {
    val builder = GraphQL.Builder()
    builder.block()
    return builder.build()
}