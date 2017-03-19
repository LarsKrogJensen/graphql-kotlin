package graphql.schema


class GraphQLNonNull(wrappedType: GraphQLType) : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLModifiedType {

    override val wrappedType: GraphQLType
        get() = _wrappedType!!

    private var _wrappedType: GraphQLType? = null

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        _wrappedType = SchemaUtil().resolveTypeReference(wrappedType, typeMap)
    }

    init {
        _wrappedType = wrappedType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as GraphQLNonNull

        return wrappedType == that.wrappedType
    }

    override fun hashCode(): Int {
        return wrappedType.hashCode()
    }

    override fun toString(): String {
        return "GraphQLNonNull{" +
                "wrappedType=" + wrappedType +
                '}'
    }

    override val name: String?
        get() = null
    
}
