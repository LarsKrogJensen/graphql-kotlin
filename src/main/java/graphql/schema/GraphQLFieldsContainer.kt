package graphql.schema


interface GraphQLFieldsContainer : GraphQLType {

    val fieldDefinitions: List<GraphQLFieldDefinition<*>>
}
