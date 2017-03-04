package graphql.schema


class GraphQLNonNull(override var wrappedType: GraphQLType) : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLModifiedType {

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        wrappedType = SchemaUtil().resolveTypeReference(wrappedType, typeMap)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as GraphQLNonNull?

        return wrappedType == that
    }

    override fun hashCode(): Int {
        return wrappedType.hashCode()
    }

    override fun toString(): String {
        return "GraphQLNonNull{" +
                "wrappedType=" + wrappedType +
                '}'
    }

    override val name: String
        get() = ""
    
}
