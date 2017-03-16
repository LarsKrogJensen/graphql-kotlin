package graphql.schema

import graphql.language.Field

/**
 * A DataFetchingEnvironment instance of passed to a [DataFetcher] as an execution context parameter
 */
interface DataFetchingEnvironment {
    /**
     * @param <T> you decide what type it is
     * *
     * *
     * @return the current object being queried
    </T> */
    fun <T> source(): T

    /**
     * @return the arguments that have been passed in via the graphql query
     */
    val arguments: Map<String, Any?>

    /**
     * Returns true of the named argument is present

     * @param name the name of the argument
     * *
     * *
     * @return true of the named argument is present
     */
    fun containsArgument(name: String): Boolean

    /**
     * Returns the named argument

     * @param name the name of the argument
     * *
     * @param <T>  you decide what type it is
     * *
     * *
     * @return the named argument or null if its not [present
    </T> */
    fun <T> argument(name: String): T?

    /**
     * Returns a context argument that is set up when the [graphql.GraphQL.execute] method
     * is invoked

     * @param <T> you decide what type it is
     * *
     * *
     * @return a context object
    </T> */
    fun <T> context(): T

    /**
     * @return the list of fields currently in query context
     */
    val fields: List<Field>

    /**
     * @return graphql type of the current field
     */
    val fieldType: GraphQLOutputType

    /**
     * @return the type of the parent of the current field
     */
    val parentType: GraphQLType

    /**
     * @return the underlying graphql schema
     */
    val graphQLSchema: GraphQLSchema
}
