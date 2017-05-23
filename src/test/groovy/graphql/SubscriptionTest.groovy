package graphql

import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

class SubscriptionTest extends Specification {

    def "evaluates subscriptions"() {
        given:
        def subscription1 = """ 
            subscription S { 
              first: changeNumberSubscribe(clientId: 101) { 
                theNumber 
              } 
            } 
            """

        def root = new NumberStore(6)

        when:
        def execute = GraphQL.newGraphQL(MutationSchemaKt.schema).build().execute(subscription1, null, root, [:])

        then:
        def fieldObservables = execute.toCompletableFuture().get().data()

        Flux<Integer> subscription = (fieldObservables["first"] as Flux<ExecutionResult>).map { exeRes ->
            exeRes.<Map<String,Integer>>data().get("theNumber")
        }

        def verifier = StepVerifier.create(subscription)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectComplete()


        root.changeNumber(1)
        root.changeNumber(2)
        root.changeNumber(3)
        root.closeFeed()

        verifier.verify()
//        execute2.toCompletableFuture().get().data() == executionResult2
        //NumberPublisher.fieldObservables == expectedResult
    }
}