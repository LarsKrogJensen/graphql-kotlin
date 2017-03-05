package graphql.language

class FragmentDefinition : AbstractNode, Definition {

    val name: String
    val typeCondition: TypeName
    var directives: List<Directive> = mutableListOf()
    var selectionSet: SelectionSet? = null

    constructor(name: String, typeCondition: TypeName) {
        this.name = name
        this.typeCondition = typeCondition
    }

    constructor(name: String, typeCondition: TypeName, selectionSet: SelectionSet) : this(name, typeCondition) {
        this.selectionSet = selectionSet
    }

    override val children: List<Node>
        get() {
            val result = mutableListOf<Node>()
            typeCondition?.let { result.add(it) }
            result.addAll(directives)
            selectionSet?.let { result.add(it) }
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as FragmentDefinition

        return name == that.name
    }

    override fun toString(): String {
        return "FragmentDefinition{" +
                "name='" + name + '\'' +
                ", typeCondition='" + typeCondition + '\'' +
                ", directives=" + directives +
                ", selectionSet=" + selectionSet +
                '}'
    }

    fun add(directive: Directive) {
        directives += directive
    }
}
