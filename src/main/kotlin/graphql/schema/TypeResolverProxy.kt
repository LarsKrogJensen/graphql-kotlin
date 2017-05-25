package graphql.schema


fun typeResolverProxy() : TypeResolver {

    return { obj: Any ->
        null
    }
}
