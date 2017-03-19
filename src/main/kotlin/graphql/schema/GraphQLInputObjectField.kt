package graphql.schema

import kotlin.properties.Delegates.notNull


class GraphQLInputObjectField(val name: String,
                              val description: String?,
                              var type: GraphQLInputType,
                              val defaultValue: Any?) {

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        type = SchemaUtil().resolveTypeReference(type, typeMap) as GraphQLInputType
    }

    @GraphQLDslMarker
    class Builder {
        var name: String by notNull<String>()
        var description: String? = null
        var defaultValue: Any? = null
        var type: GraphQLInputType by notNull<GraphQLInputType>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun type(type: GraphQLInputObjectType.Builder): Builder {
            return type(type.build())
        }

        fun type(type: GraphQLInputType): Builder {
            this.type = type
            return this
        }

        fun defaultValue(defaultValue: Any): Builder {
            this.defaultValue = defaultValue
            return this
        }

        fun build(): GraphQLInputObjectField {
            return GraphQLInputObjectField(name, description, type, defaultValue)
        }
    }

    companion object {

        @JvmStatic
        fun newInputObjectField(): Builder {
            return Builder()
        }
    }
}

inline fun newInputField(block: GraphQLInputObjectField.Builder.() -> Unit): GraphQLInputObjectField {
    val builder = GraphQLInputObjectField.Builder()
    builder.block()
    return builder.build()
}