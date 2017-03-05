package graphql.language


class Field : AbstractNode, Selection {
    val name: String
    var alias: String? = null
    var arguments: List<Argument> = mutableListOf()
    var directives: List<Directive> = mutableListOf()
    var selectionSet: SelectionSet? = null

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, selectionSet: SelectionSet) : this(name) {
        this.selectionSet = selectionSet
    }

    constructor(name: String, arguments: List<Argument>) : this(name) {
        this.arguments = arguments
    }

    constructor(name: String, arguments: List<Argument>, directives: List<Directive>) : this(name, arguments) {
        this.directives = directives
    }

    constructor(name: String, arguments: List<Argument>, selectionSet: SelectionSet) : this(name, arguments) {
        this.selectionSet = selectionSet
    }

    override val children: List<Node>
        get() {
            val result: MutableList<Node> = mutableListOf()
            result.addAll(arguments)
            result.addAll(directives)

            if (selectionSet != null)
                result.add(selectionSet!!)

            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val field = node as Field

        if (name != field.name) return false
        return !if (alias != null) alias != field.alias else field.alias != null
    }

    override fun toString(): String {
        return "Field{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", arguments=" + arguments +
                ", directives=" + directives +
                ", selectionSet=" + selectionSet +
                '}'
    }

    fun add(directive: Directive) {
        directives += directive
    }

    fun add(argument: Argument) {
        arguments += argument
    }
}
