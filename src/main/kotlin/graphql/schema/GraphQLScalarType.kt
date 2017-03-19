package graphql.schema


class GraphQLScalarType(override val name: String,
                        val description: String?,
                        val coercing: Coercing<*, *>)
    : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLUnmodifiedType, GraphQLNullableType {


    override fun toString(): String {
        return "GraphQLScalarType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coercing=" + coercing +
                '}'
    }
}
