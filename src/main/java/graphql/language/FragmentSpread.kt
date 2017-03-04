package graphql.language


import java.util.ArrayList

class FragmentSpread : AbstractNode, Selection {

    var name: String? = null
    var directives: List<Directive> = emptyList()

    constructor() {}

    constructor(name: String) {
        this.name = name
    }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as FragmentSpread

        return !if (name != null) name != that.name else that.name != null

    }


    override val children: List<Node>
        get() = directives

    override fun toString(): String {
        return "FragmentSpread{" +
                "name='" + name + '\'' +
                ", directives=" + directives +
                '}'
    }

    fun add(directive: Directive) {
        directives += directive
    }
}
