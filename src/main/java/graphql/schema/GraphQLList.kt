package graphql.schema

class GraphQLList(wrapType: GraphQLType) : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLModifiedType, GraphQLNullableType {
    private var _wrappedType: GraphQLType = wrapType

    override val wrappedType: GraphQLType
        get() = _wrappedType

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        _wrappedType = SchemaUtil().resolveTypeReference(wrappedType, typeMap)
    }

    override fun equals(other: Any?) =
            when (other) {
                null            -> false
                !is GraphQLList -> false
                else            -> wrappedType == other.wrappedType
            }

    override fun hashCode(): Int {
        return wrappedType.hashCode()
    }

    override fun toString(): String {
        return "GraphQLList{" +
                "wrappedType=" + wrappedType +
                '}'
    }

    override val name: String?
        get() = null
}
