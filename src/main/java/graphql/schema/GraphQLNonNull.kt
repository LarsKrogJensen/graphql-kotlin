package graphql.schema

import kotlin.properties.Delegates


class GraphQLNonNull(wrappedType: GraphQLType) : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLModifiedType {

    private var _wrappedType: GraphQLType by Delegates.notNull<GraphQLType>()

    override val wrappedType = _wrappedType

    init {
        _wrappedType = wrappedType
    }

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        _wrappedType = SchemaUtil().resolveTypeReference(wrappedType, typeMap)
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
