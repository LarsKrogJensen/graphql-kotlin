package graphql.schema


interface GraphQLModifiedType : GraphQLType {
    val wrappedType: GraphQLType
}
