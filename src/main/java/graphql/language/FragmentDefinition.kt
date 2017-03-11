package graphql.language

class FragmentDefinition : AbstractNode, Definition {

    val name: String
    val typeCondition: TypeName
    val directives: MutableList<Directive> = mutableListOf()
    var selectionSet: SelectionSet = SelectionSet()

    constructor(name: String, typeCondition: TypeName) {
        this.name = name
        this.typeCondition = typeCondition
    }

    constructor(name: String, typeCondition: TypeName,
                selectionSet: SelectionSet) : this(name, typeCondition) {
        this.selectionSet.selections().addAll(selectionSet.selections())
    }

    override val children: List<Node>
        get() {
            val result = mutableListOf<Node>()
            result.add(typeCondition)
            result.addAll(directives)
            if (!selectionSet.isEmpty())
                result.add(selectionSet)
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
