package graphql.language


data class Directive(var name: String) : AbstractNode() {
    val arguments = mutableListOf<Argument>()

    constructor(name: String, arguments: List<Argument>) : this(name) {
        this.arguments.addAll(arguments)
    }
    override val children: List<Node>
        get() = arguments

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val directive = node as Directive

        return name == directive.name
    }


}
