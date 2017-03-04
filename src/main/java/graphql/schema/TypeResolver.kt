package graphql.schema


interface TypeResolver {
    fun getType(obj: Any): GraphQLObjectType
}
