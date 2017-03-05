package graphql.language


data class ObjectField(val name: String, val value: Value) : AbstractNode() {

    override val children: List<Node>
        get() = listOf(value)

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as ObjectField

        return name == that.name
    }
}
