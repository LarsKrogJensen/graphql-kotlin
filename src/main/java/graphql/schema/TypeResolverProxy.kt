package graphql.schema


fun typeResolverProxy() : TypeResolver {

    return { obj: Any ->
        null
    }
}
//class TypeResolverProxy : TypeResolver {
//
//    var typeResolver: TypeResolver? = null
//
//    override fun getType(obj: Any): GraphQLObjectType? {
//        return typeResolver?.(obj)
//    }
//}
