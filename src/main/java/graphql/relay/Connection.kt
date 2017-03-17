package graphql.relay

/**
 * represents a connection in relay.
 */
interface Connection<out T> {

    val edges: List<Edge<T>>

    val pageInfo: PageInfo

}
