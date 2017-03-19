package graphql.language



data class EnumValue(var name: String) : AbstractNode(), Value {
    override val children: List<Node>
        get() = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val enumValue = node as EnumValue

        return name == enumValue.name
    }
}
