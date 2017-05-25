package graphql.schema

import kotlin.properties.Delegates

open class GraphQLSubscriptionObjectType(
    name: String,
    description: String?,
    fieldDefinitions: List<GraphQLFieldDefinition<*>>
) : GraphQLObjectType(name, description, fieldDefinitions, emptyList()) {

    @GraphQLDslMarker
    class Builder {
        var name: String by Delegates.notNull<String>()
        var description: String? = null
        val fields = mutableListOf<GraphQLFieldDefinition<*>>()

        inline fun <reified TOut : Any> subscription(block: GraphQLFieldDefinition.Builder<TOut>.() -> Unit) {
            this.fields += newField(block)
        }

        fun build(): GraphQLSubscriptionObjectType {
            return GraphQLSubscriptionObjectType(name, description, fields)
        }
    }
}

fun newSubscriptionObject(block: GraphQLSubscriptionObjectType.Builder.() -> Unit): GraphQLSubscriptionObjectType {
    val builder = GraphQLSubscriptionObjectType.Builder()
    builder.block()
    return builder.build()
}