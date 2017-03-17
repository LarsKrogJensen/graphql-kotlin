package graphql.relay

data class DefaultPageInfo(
        override var startCursor: ConnectionCursor? = null,
        override var endCursor: ConnectionCursor? = null,
        override var isHasPreviousPage: Boolean = false,
        override var isHasNextPage: Boolean = false 
) : PageInfo
