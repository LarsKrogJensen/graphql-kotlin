package graphql.schema


import graphql.language.Field

class DataFetchingEnvironmentImpl(private val source: Any,
                                  override val arguments: Map<String, Any>,
                                  private val context: Any,
                                  override val fields: List<Field>,
                                  override val fieldType: GraphQLOutputType,
                                  override val parentType: GraphQLType,
                                  override val graphQLSchema: GraphQLSchema) : DataFetchingEnvironment {

    override fun <T> source(): T {
        return source as T
    }

    override fun containsArgument(name: String): Boolean {
        return arguments.containsKey(name)
    }

    override fun <T> argument(name: String): T {
        return arguments[name] as T
    }

    override fun <T> context(): T {
        return context as T
    }

}
