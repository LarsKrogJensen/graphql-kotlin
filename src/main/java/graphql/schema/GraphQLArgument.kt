package graphql.schema

import kotlin.properties.Delegates.notNull


class GraphQLArgument(val name: String,
                      val description: String,
                      type: GraphQLInputType,
                      val defaultValue: Any?) {

    var type: GraphQLInputType = type
        get() = field
        private set(value) {
            field = value
        }

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        type = SchemaUtil().resolveTypeReference(type, typeMap) as GraphQLInputType
    }

    class Builder {

        private var name: String by notNull<String>()
        private var type: GraphQLInputType by notNull<GraphQLInputType>()
        private var defaultValue: Any? = null
        private var description: String = "No description available."

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun type(type: GraphQLInputType): Builder {
            this.type = type
            return this
        }

        fun defaultValue(defaultValue: Any): Builder {
            this.defaultValue = defaultValue
            return this
        }

        fun build(): GraphQLArgument {
            return GraphQLArgument(name, description, type, defaultValue)
        }


    }

    companion object {
        @JvmStatic
        fun newArgument(): GraphQLArgument.Builder {
            return GraphQLArgument.Builder()
        }
    }
}

fun newArgument(): GraphQLArgument.Builder {
    return GraphQLArgument.Builder()
}

