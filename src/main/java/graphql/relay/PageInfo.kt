package graphql.relay


class PageInfo {
    var startCursor: ConnectionCursor? = null
    var endCursor: ConnectionCursor? = null
    var isHasPreviousPage: Boolean = false
    var isHasNextPage: Boolean = false
}
