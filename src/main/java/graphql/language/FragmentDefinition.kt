package graphql.language

class FragmentDefinition : AbstractNode, Definition {

    var name: String? = null
    var typeCondition: TypeName? = null
    var directives: List<Directive> = mutableListOf()
    var selectionSet: SelectionSet? = null

    constructor()

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

        return !if (name != null) name != that.name else that.name != null
    }

    override fun toString(): String {
        return "FragmentDefinition{" +
                "name='" + name + '\'' +
                ", typeCondition='" + typeCondition + '\'' +
                ", directives=" + directives +
                ", selectionSet=" + selectionSet +
                '}'
    }
}
