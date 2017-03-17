package graphql.relay

data class DefaultConnection<T>
(
        override val edges: List<Edge<T>>,
        override val pageInfo: PageInfo
) : Connection<T>

