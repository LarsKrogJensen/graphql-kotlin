package graphql.relay

data class DefaultEdge<out T>(
        override val node: T,
        override val cursor: ConnectionCursor
) : Edge<T>

