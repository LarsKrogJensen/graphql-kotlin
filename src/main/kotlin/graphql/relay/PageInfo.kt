package graphql.relay

/**
 * represents a page in relay.
 */
interface PageInfo {

    val startCursor: ConnectionCursor?

    val endCursor: ConnectionCursor?

    val isHasPreviousPage: Boolean

    val isHasNextPage: Boolean

}
