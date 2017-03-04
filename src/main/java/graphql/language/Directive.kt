package graphql.language


import java.util.ArrayList

data class Directive(var name: String) : AbstractNode() {
    private val arguments = mutableListOf<Argument>()

    constructor(name: String, arguments: List<Argument>) : this(name) {
        this.arguments.addAll(arguments)
    }

    fun getArguments(): List<Argument> = arguments

    override val children: List<Node>
        get() = ArrayList<Node>(arguments)

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val directive = node as Directive

        return name == directive.name
    }

    fun add(argument: Argument) {
        arguments += argument
    }

}
