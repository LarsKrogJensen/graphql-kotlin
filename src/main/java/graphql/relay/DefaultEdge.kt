package graphql.relay

data class DefaultEdge<T>(
        override val node: T,
        override val cursor: ConnectionCursor
) : Edge<T>

