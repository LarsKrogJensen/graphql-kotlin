package graphql.schema



class GraphQLList(override var wrappedType: GraphQLType) : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLModifiedType, GraphQLNullableType {

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        wrappedType = SchemaUtil().resolveTypeReference(wrappedType, typeMap)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as GraphQLList?

        return wrappedType == other.wrappedType

    }

    override fun hashCode(): Int {
        return wrappedType.hashCode()
    }

    override val name: String
        get() = ""
}
