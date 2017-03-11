package graphql.language


class InlineFragment : AbstractNode, Selection {
    val typeCondition: TypeName?
    val directives: MutableList<Directive>
    var selectionSet: SelectionSet


    constructor(typeCondition: TypeName?) {
        this.typeCondition = typeCondition
        this.directives = ArrayList()
        this.selectionSet = SelectionSet()
    }

    constructor(typeCondition: TypeName, directives: List<Directive>, selectionSet: SelectionSet) {
        this.typeCondition = typeCondition
        this.directives = ArrayList(directives)
        this.selectionSet = selectionSet
    }

    constructor(typeCondition: TypeName, selectionSet: SelectionSet){
        this.typeCondition = typeCondition
        this.selectionSet = selectionSet
        this.directives = ArrayList()
    }

    override val children: List<Node>
        get() {
            val result = mutableListOf<Node>()
            if (typeCondition != null) {
                result.add(typeCondition)
            }
            result.addAll(directives)
            if (!selectionSet.isEmpty())
                result.add(selectionSet)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }


    override fun toString(): String {
        return "InlineFragment{" +
                "typeCondition='" + typeCondition + '\'' +
                ", directives=" + directives +
                ", selectionSet=" + selectionSet +
                '}'
    }

    fun add(directive: Directive) {
        directives += directive
    }
}
