package graphql.language


import java.util.ArrayList

class InlineFragment : AbstractNode, Selection {
    var typeCondition: TypeName? = null
    var directives: List<Directive> = ArrayList()
    var selectionSet: SelectionSet? = null

    constructor()

    constructor(typeCondition: TypeName) {
        this.typeCondition = typeCondition
    }

    constructor(typeCondition: TypeName, directives: List<Directive>, selectionSet: SelectionSet) {
        this.typeCondition = typeCondition
        this.directives = directives
        this.selectionSet = selectionSet
    }

    constructor(typeCondition: TypeName, selectionSet: SelectionSet) {
        this.typeCondition = typeCondition
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
