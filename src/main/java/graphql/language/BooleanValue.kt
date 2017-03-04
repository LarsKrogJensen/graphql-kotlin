package graphql.language

class BooleanValue(var value: Boolean) : AbstractNode(), Value {
    override val children: List<Node>
        get() = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as BooleanValue

        return value == that.value
    }
}
