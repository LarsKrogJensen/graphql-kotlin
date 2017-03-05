package graphql.relay


data class Edge<T>(var node: T, var cursor: ConnectionCursor)
