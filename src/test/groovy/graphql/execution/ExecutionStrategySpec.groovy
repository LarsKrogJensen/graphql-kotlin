package graphql.execution

import graphql.ExecutionResult
import graphql.language.Field
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import java.util.concurrent.CompletionStage

class ExecutionStrategySpec extends Specification {

    ExecutionStrategy executionStrategy

    def setup() {
        executionStrategy = new ExecutionStrategy() {
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
        new ExecutionContext(null, null, null, executionStrategy, executionStrategy, null, null, null, null)
    }

    def "completes value for a java.util.List"() {
        given:
        ExecutionContext executionContext = buildContext()
        Field field = new Field("test")
        def fieldType = new GraphQLList(Scalars.GraphQLString)
        def result = Arrays.asList("test")
        when:
        def executionResult = executionStrategy.completeValue(executionContext, fieldType, [field], result)

        then:
        executionResult.data == ["test"]
    }

    def "completes value for an array"() {
        given:
        ExecutionContext executionContext = buildContext()
        Field field = new Field("test")
        def fieldType = new GraphQLList(Scalars.GraphQLString)
        String[] result = ["test"]
        when:
        def executionResult = executionStrategy.completeValue(executionContext, fieldType, [field], result)

        then:
        executionResult.data == ["test"]
    }

}
