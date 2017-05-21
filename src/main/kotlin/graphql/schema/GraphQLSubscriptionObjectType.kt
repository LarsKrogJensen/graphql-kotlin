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

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

//        fun <T> field(fieldDefinition: GraphQLFieldDefinition<T>): Builder {
//            Assert.assertNotNull(fieldDefinition, "fieldDefinition can't be null")
//            this.fields.add(fieldDefinition)
//            return this
//        }
//
//        fun <T> field(builder: GraphQLFieldDefinition.Builder<T>): Builder {
//            this.fields.add(builder.build())
//            return this
//        }

        inline fun <reified TOut : Any> field(block: GraphQLFieldDefinition.Builder<TOut>.() -> Unit) {
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