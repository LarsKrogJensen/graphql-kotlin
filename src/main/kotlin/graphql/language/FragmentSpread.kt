package graphql.language



class FragmentSpread(val name: String) : AbstractNode(), Selection {

    val directives: MutableList<Directive> = mutableListOf()

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
