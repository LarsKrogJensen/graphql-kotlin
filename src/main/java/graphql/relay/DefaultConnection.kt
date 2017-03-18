package graphql.relay

data class DefaultConnection<out T>
(
        override val edges: List<Edge<T>>,
        override val pageInfo: PageInfo
) : Connection<T>

