package graphql.language


import java.util.ArrayList

data class Directive(var name: String) : AbstractNode() {
    private val _arguments = mutableListOf<Argument>()

    constructor(name: String, arguments: List<Argument>) : this(name) {
        this._arguments.addAll(arguments)
    }

    val arguments: List<Argument>
        get() = _arguments

    override val children: List<Node>
        get() = ArrayList<Node>(_arguments)

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val directive = node as Directive

        return name == directive.name
    }

    fun add(argument: Argument) {
        _arguments += argument
    }

}
