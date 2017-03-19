package graphql.execution

//package graphql.execution
//
//import graphql.ExecutionResult
//import graphql.GraphQLString
//import graphql.execution.instrumentation.NoOpInstrumentation
//import graphql.language.Field
//import graphql.language.OperationDefinition
//import graphql.schema.GraphQLList
//import graphql.schema.GraphQLObjectType
//import graphql.schema.GraphQLSchema
//import org.jetbrains.spek.api.Spek
//import org.jetbrains.spek.api.dsl.describe
//import org.jetbrains.spek.api.dsl.it
//import org.jetbrains.spek.api.dsl.on
//import org.junit.platform.runner.JUnitPlatform
//import org.junit.runner.RunWith
//import java.util.concurrent.CompletableFuture
//import java.util.concurrent.CompletionStage
//import kotlin.test.assertEquals
//
//@RunWith(JUnitPlatform::class)
//object ExecutionStrategy2Spec : Spek(
//        {
//            describe("exection strategies") {
//                val executionStrategy = object : ExecutionStrategy() {
//                    override fun execute(executionContext: ExecutionContext, parentType: GraphQLObjectType, source: Any, fields: Map<String, List<Field>>): CompletionStage<ExecutionResult> {
//                        return CompletableFuture.completedFuture(null)
//                    }
//                }
//
//                val executionContext: ExecutionContext = ExecutionContext(NoOpInstrumentation.INSTANCE,
//                                                                          ExecutionId("1"),
//                                                                          GraphQLSchema.newSchema {  },
//                                                                          executionStrategy,
//                                                                          executionStrategy,
//                                                                          mapOf(),
//                                                                          OperationDefinition(OperationDefinition.Operation.QUERY),
//                                                                          mapOf(),
//                                                                          Any())
//                val field: Field = Field("test")
//                val fieldType = GraphQLList(GraphQLString)
//                val result = listOf<String>("test")
//
//                on("completes value for a java.util.List") {
//                    val executionResult = executionStrategy.completeValue(executionContext, fieldType, listOf(field), result)
//                            .toCompletableFuture().get()
//
//                    it("should return a list of strings containing one element") {
//                        assertEquals(listOf("test"), executionResult.data())
//                    }
//                }
//
//            }
//        })