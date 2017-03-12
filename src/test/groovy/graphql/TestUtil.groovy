package graphql

import graphql.schema.*


class TestUtil {
    static GraphQLSchema schemaWithInputType(GraphQLInputType inputType) {
        GraphQLArgument.Builder fieldArgument = GraphQLArgumentKt.newArgument().name("arg").type(inputType)
        GraphQLFieldDefinition.Builder name = GraphQLFieldDefinition.newFieldDefinition()
                .name("name").type(ScalarsKt.GraphQLString).argument(fieldArgument)
        GraphQLObjectType queryType = GraphQLObjectType.newObject().name("query").field(name).build()
        GraphQLSchema.newSchema().query(queryType).build(new HashSet())
    }

    static dummySchema = GraphQLSchema.newSchema()
            .query(GraphQLObjectType.newObject()
            .name("QueryType")
            .build())
            .build(new HashSet())
}
