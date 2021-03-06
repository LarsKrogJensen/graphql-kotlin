package graphql.execution

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.StarWarsSchemaKt
import graphql.language.Field
import graphql.schema.GraphQLObjectType
import spock.lang.Specification

import java.util.concurrent.CompletionStage

class ExecutionIdTest extends Specification {

    class CaptureIdStrategy extends SimpleExecutionStrategy {
        ExecutionId executionId = null

        @Override
        CompletionStage<ExecutionResult> execute(ExecutionContext executionContext, GraphQLObjectType parentType, Object source, Map<String, List<Field>> fields) {
            executionId = executionContext.executionId
            return super.execute(executionContext, parentType, source, fields)
        }
    }

    def query = """
        query HeroNameAndFriendsQuery {
            hero {
                id
            }
        }
        """


    def 'Ensures that an execution identifier is present by default'() {

        when:

        CaptureIdStrategy idStrategy = new CaptureIdStrategy()

        GraphQL.newGraphQL(StarWarsSchemaKt.starWarsSchema).queryExecutionStrategy(idStrategy).build().execute(query)

        then:

        idStrategy.executionId != null
    }

    def 'Ensures that an execution identifier provider is able to be specified'() {

        when:

        CaptureIdStrategy idStrategy = new CaptureIdStrategy()

        def specificProvider = new ExecutionIdProvider() {
            long count = 0

            @Override
            ExecutionId provide(String query, String operationName, Object context) {
                count++
                return new ExecutionId(count.toString())
            }
        }
        def graphQL = GraphQL.newGraphQL(StarWarsSchemaKt.starWarsSchema)
                .executionIdProvider(specificProvider)
                .queryExecutionStrategy(idStrategy)
                .build()

        // execute multiple times to ensure its called multiple times
        graphQL.execute(query)
        def id1 = idStrategy.executionId.toString()

        graphQL.execute(query)
        def id2 = idStrategy.executionId.toString()

        then:

        id1 == "1"
        id2 == "2"
    }
}
