package graphql.schema

import kotlin.properties.Delegates


class GraphQLList(wrappedType: GraphQLType) : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLModifiedType, GraphQLNullableType {
    private var _wrappedType: GraphQLType by Delegates.notNull<GraphQLType>()

    override val wrappedType = _wrappedType

    init {
        _wrappedType = wrappedType
    }

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

    override val name: String
        get() = ""
}
