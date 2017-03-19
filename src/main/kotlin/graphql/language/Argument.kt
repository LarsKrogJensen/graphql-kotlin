package graphql.language

data class Argument(var name: String, var value: Value) : AbstractNode() {
    override val children: List<Node>
        get() = listOf(value)


    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val argument = node as Argument

        return name == argument.name
    }
}
