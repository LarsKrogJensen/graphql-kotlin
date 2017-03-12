package graphql.execution

import graphql.ExecutionResult
import graphql.ScalarsKt
import graphql.StarWarsSchema
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.language.Field
import graphql.language.FragmentDefinition
import graphql.language.OperationDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import java.util.concurrent.CompletionStage

class ExecutionStrategySpec extends Specification {

    IExecutionStrategy executionStrategy

    def setup() {
        executionStrategy = new AbstractExecutionStrategy() {
            @Override
            CompletionStage<ExecutionResult> execute(
                    @NotNull ExecutionContext executionContext,
                    @NotNull GraphQLObjectType parentType,
                    @NotNull Object source, @NotNull Map<String, ? extends List<Field>> fields) {
                return null
            }
        }
    }

    def buildContext() {
        new ExecutionContext(
                NoOpInstrumentation.INSTANCE,
                new ExecutionId("1"),
                StarWarsSchema.INSTANCE.starWarsSchema,
                executionStrategy,
                executionStrategy,
                new HashMap<String, FragmentDefinition>(),
                new OperationDefinition(OperationDefinition.Operation.QUERY),
                new HashMap(),
                new Object())
    }

    def "completes value for a java.util.List"() {
        given:
        ExecutionContext executionContext = buildContext()
        Field field = new Field("test")
        def fieldType = new GraphQLList(ScalarsKt.GraphQLString)
        def result = Arrays.asList("test")

        when:
        def executionResult = executionStrategy.completeValue(executionContext, fieldType, [field], result)

        then:
        executionResult.toCompletableFuture().get().data() == ["test"]
    }

    def "completes value for an array"() {
        given:
        ExecutionContext executionContext = buildContext()
        Field field = new Field("test")
        def fieldType = new GraphQLList(ScalarsKt.GraphQLString)
        String[] result = ["test"]
        when:
        def executionResult = executionStrategy.completeValue(executionContext, fieldType, [field], result)

        then:
        executionResult.toCompletableFuture().get().data() == ["test"]
    }

}
