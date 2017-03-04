package graphql.schema

class GraphQLEnumValueDefinition (val name: String,
                                  val description: String?,
                                  val value: Any?,
                                  val deprecationReason: String? = null) {

    val deprecated: Boolean
        get() = deprecationReason != null
}
