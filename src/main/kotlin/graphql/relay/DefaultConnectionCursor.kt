package graphql.relay

data class DefaultConnectionCursor(override val value: String) : ConnectionCursor  {
    override fun toString(): String {
        return value
    }
}



