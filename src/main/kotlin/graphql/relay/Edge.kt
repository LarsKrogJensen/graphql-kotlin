package graphql.relay

/**
 * represents an edge in relay.
 */
interface Edge<out T> {

    val node: T?

    val cursor: ConnectionCursor

}
