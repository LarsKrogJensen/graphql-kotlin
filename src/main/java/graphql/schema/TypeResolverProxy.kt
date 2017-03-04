package graphql.schema


class TypeResolverProxy : TypeResolver {

    var typeResolver: TypeResolver? = null

    override fun getType(obj: Any): GraphQLObjectType {
        return if (typeResolver != null) typeResolver!!.getType(obj) else null
    }
}
