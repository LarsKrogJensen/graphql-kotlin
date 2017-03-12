package graphql.execution

import graphql.MutationSchema
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.parser.Parser
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class ExecutionTest extends Specification {

    def parser = new Parser()
    def mutationStrategy = Mock(IExecutionStrategy)
    def queryStrategy = Mock(IExecutionStrategy)
    def execution = new Execution(queryStrategy, mutationStrategy, NoOpInstrumentation.INSTANCE)

    def "query strategy is used for query requests"() {
        given:
        //def mutationStrategy = Mock(ExecutionStrategy)
        queryStrategy.execute(_,_,_,_) >> CompletableFuture.completedFuture(null)

        def execution = new Execution(queryStrategy, mutationStrategy, NoOpInstrumentation.INSTANCE)

        def query = '''
            query {
                numberHolder {
                    theNumber
                }
            }
        '''
        def document = parser.parseDocument(query)

        when:
        execution.execute(new ExecutionId("123"), MutationSchema.schema, new Object(), document, null, new HashMap<>())

        then:
        1 * queryStrategy.execute(*_)
        0 * mutationStrategy.execute(*_)
    }

    def "mutation strategy is used for mutation requests"() {
        given:
        def query = '''
            mutation {
                changeTheNumber(newNumber: 1) {
                    theNumber
                }
            }
        '''
        def document = parser.parseDocument(query)
        queryStrategy.execute(*_) >> CompletableFuture.completedFuture(null)
        
        when:
        execution.execute(new ExecutionId("aasas"), MutationSchema.schema, new Object(), document, null, new HashMap<>())

        then:
        0 * queryStrategy.execute(*_)
        1 * mutationStrategy.execute(*_)
    }
}
