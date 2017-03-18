package graphql.schema


import graphql.introspection.Introspection.DirectiveLocation
import kotlin.properties.Delegates.notNull

class GraphQLDirective(val name: String,
                       val description: String?,
                       val locations: Set<DirectiveLocation>,
                       val arguments: List<GraphQLArgument>) {

    fun argument(name: String): GraphQLArgument? {
        for (argument in arguments) {
            if (argument.name == name) return argument
        }
        return null
    }

    @GraphQLDslMarker
    class Builder {
        var name: String by notNull<String>()
        val locations = mutableSetOf<DirectiveLocation>()
        val arguments = mutableListOf<GraphQLArgument>()
        var description: String? = null

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun argument(fieldArgument: GraphQLArgument): Builder {
            arguments.add(fieldArgument)
            return this
        }

        fun argument(builder: GraphQLArgument.Builder): Builder {
            this.arguments.add(builder.build())
            return this
        }

        fun argument(block: GraphQLArgument.Builder.() -> Unit) {
            arguments += newArgument(block)
        }

        fun build(): GraphQLDirective {
            return GraphQLDirective(name, description, locations, arguments)
        }
    }

    companion object {

        @JvmStatic
        fun newDirective(): Builder {
            return Builder()
        }
    }
}

fun newDirective(block: GraphQLDirective.Builder.() -> Unit ): GraphQLDirective {
    val builder = GraphQLDirective.Builder()
    builder.block()
    return builder.build()
}
