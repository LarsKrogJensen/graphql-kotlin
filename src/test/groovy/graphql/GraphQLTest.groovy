package graphql

import graphql.language.SourceLocation
import graphql.schema.*
import graphql.validation.ValidationErrorType
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

import static graphql.ScalarsKt.GraphQLString
import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLSchema.newSchema

class GraphQLTest extends Specification {


    def "simple query"() {
        given:
        GraphQLFieldDefinition.Builder fieldDefinition = newFieldDefinition()
                .name("hello")
                .type(GraphQLString)
                .staticValue("world")
        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .field(fieldDefinition)
                        .build()
        ).build(new HashSet<GraphQLType>())

        when:
        def result = GraphQL.newGraphQL(schema).build().execute('{ hello }').toCompletableFuture().get().data()

        then:
        result == [hello: 'world']

    }

    def "query with sub-fields"() {
        given:
        GraphQLObjectType heroType = newObject()
                .name("heroType")
                .field(
                newFieldDefinition()
                        .name("id")
                        .type(GraphQLString))
                .field(
                newFieldDefinition()
                        .name("name")
                        .type(GraphQLString))
                .build()

        GraphQLFieldDefinition.Builder simpsonField = newFieldDefinition()
                .name("simpson")
                .type(heroType)
                .staticValue([id: '123', name: 'homer'])

        GraphQLSchema graphQLSchema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .field(simpsonField)
                        .build()
        ).build(new HashSet<GraphQLType>())

        when:
        def result = GraphQL.newGraphQL(graphQLSchema).build().execute('{ simpson { id, name } }').toCompletableFuture().get().data()

        then:
        result == [simpson: [id: '123', name: 'homer']]
    }

    def "query with validation errors"() {
        given:
        GraphQLFieldDefinition.Builder fieldDefinition = newFieldDefinition()
                .name("hello")
                .type(GraphQLString)
                .argument(newArgument().name("arg").type(GraphQLString))
                .staticValue("world")
        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .field(fieldDefinition)
                        .build()
        ).build(new HashSet<GraphQLType>())

        when:
        def errors = GraphQL.newGraphQL(schema).build().execute('{ hello(arg:11) }').toCompletableFuture().get().errors

        then:
        errors.size() == 1
    }

    def "query with invalid syntax"() {
        given:
        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .build()
        ).build(new HashSet<GraphQLType>())

        when:
        def errors = GraphQL.newGraphQL(schema).build().execute('{ hello(() }').toCompletableFuture().get().errors

        then:
        errors.size() == 1
        errors[0].errorType() == ErrorType.InvalidSyntax
        errors[0].locations() == [new SourceLocation(1, 8)]
    }

    def "query with invalid syntax 2"() {
        given:
        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .build()
        ).build(new HashSet<GraphQLType>())

        when:
        def errors = GraphQL.newGraphQL(schema).build().execute('{ hello[](() }').toCompletableFuture().get().errors

        then:
        errors.size() == 1
        errors[0].errorType() == ErrorType.InvalidSyntax
        errors[0].locations() == [new SourceLocation(1, 7)]
    }

    def "non null argument is missing"() {
        given:
        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .field(newFieldDefinition()
                        .name("field")
                        .type(GraphQLString)
                        .argument(newArgument()
                        .name("arg")
                        .type(new GraphQLNonNull(GraphQLString))))
                        .build()
        ).build(new HashSet<GraphQLType>())

        when:
        def errors = GraphQL.newGraphQL(schema).build().execute('{ field }').toCompletableFuture().get().errors

        then:
        errors.size() == 1
        errors[0].errorType() == ErrorType.ValidationError
        errors[0].validationErrorType == ValidationErrorType.MissingFieldArgument
        errors[0].locations() == [new SourceLocation(1, 3)]
    }

    def "`Iterable` can be used as a `GraphQLList` field result"() {
        given:
        def set = new HashSet<String>()
        set.add("One")
        set.add("Two")

        def schema = newSchema()
          .query(newObject()
            .name("QueryType")
            .field(newFieldDefinition()
              .name("set")
              .type(new GraphQLList(GraphQLString))
              .fetcher({ CompletableFuture.completedFuture(['One', 'Two']) })))
          .build(new HashSet<GraphQLType>())

        when:
        def data = GraphQL.newGraphQL(schema).build().execute("query { set }").toCompletableFuture().get().data()

        then:
        data == [set: ['One', 'Two']]
    }

    def "document with two operations executes specified operation"() {
        given:

        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .field(newFieldDefinition().name("field1").type(GraphQLString).fetcher(DataFetcherKt.staticDataFetcher("value1")))
                        .field(newFieldDefinition().name("field2").type(GraphQLString).fetcher(DataFetcherKt.staticDataFetcher("value2")))
        )
                .build(new HashSet<GraphQLType>())

        def query = """
        query Query1 { field1 }
        query Query2 { field2 }
        """

        def expected = [field2: 'value2']

        when:
        def result = GraphQL.newGraphQL(schema).build()
                .execute(query, 'Query2', new Object(), [:]).toCompletableFuture().get()

        then:
        result.data() == expected
        result.errors.size() == 0
    }

    def "document with two operations but no specified operation throws"() {
        given:

        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("RootQueryType")
                        .field(newFieldDefinition().name("name").type(GraphQLString))
        )
        .build(new HashSet<GraphQLType>())

        def query = """
        query Query1 { name }
        query Query2 { name }
        """

        when:
        GraphQL.newGraphQL(schema).build().execute(query).toCompletableFuture().get()

        then:
        thrown(ExecutionException)
    }
}
