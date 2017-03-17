package graphql.schema

import kotlin.properties.Delegates

class GraphQLEnumValueDefinition (val name: String,
                                  val description: String?,
                                  val value: Any?,
                                  val deprecationReason: String? = null) {

    val deprecated: Boolean
        get() = deprecationReason != null

    @GraphQLDslMarker
    class Builder  {
        var name: String by Delegates.notNull<String>()
        var description: String? = null
        var value: Any? = null
        var deprecationReason: String? = null

        fun build() = GraphQLEnumValueDefinition(name, description, value, deprecationReason)
    }
}
