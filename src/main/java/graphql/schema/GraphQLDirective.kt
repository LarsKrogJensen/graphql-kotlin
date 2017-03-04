package graphql.schema


import graphql.introspection.Introspection.DirectiveLocation
import kotlin.properties.Delegates.notNull

class GraphQLDirective(val name: String,
                       val description: String?,
                       val locations: Set<DirectiveLocation>,
                       val arguments: List<GraphQLArgument>) {

    class Builder {
        private var name: String by notNull<String>()
        private val locations = mutableSetOf<DirectiveLocation>()
        private val arguments = mutableListOf<GraphQLArgument>()
        private var description: String? = null

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun validLocations(vararg validLocations: DirectiveLocation): Builder {
            for (location in validLocations) {
                locations.add(location)
            }
            return this
        }

        fun argument(fieldArgument: GraphQLArgument): Builder {
            arguments.add(fieldArgument)
            return this
        }

        fun argument(builderFunction: BuilderFunction<GraphQLArgument.Builder>): Builder {
            var builder: GraphQLArgument.Builder = GraphQLArgument.newArgument()
            builder = builderFunction.apply(builder)
            return argument(builder)
        }

        fun argument(builder: GraphQLArgument.Builder): Builder {
            this.arguments.add(builder.build())
            return this
        }

        fun build(): GraphQLDirective {
            return GraphQLDirective(name, description, locations, arguments)
        }
    }

    companion object {

        fun newDirective(): Builder {
            return Builder()
        }
    }
}
