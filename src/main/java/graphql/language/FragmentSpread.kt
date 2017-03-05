package graphql.language


import java.util.ArrayList

class FragmentSpread(val name: String) : AbstractNode(), Selection {

    var directives: List<Directive> = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as FragmentSpread

        return name == that.name
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
