package graphql.language


data class StringValue(var value: String?) : AbstractNode(), Value {

    override val children: List<Node>
        get() = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as StringValue

        return !if (value != null) value != that.value else that.value != null
    }

}
