package graphql.language

class EnumValueDefinition(val name: String,
                          directives: List<Directive>? = null) : AbstractNode() {
    val directives: MutableList<Directive> = mutableListOf()

    init {
        if (null != directives)
            this.directives.addAll(directives)
    }
    override val children: List<Node>
        get() = directives

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as EnumValueDefinition
        if (name != that.name) {
            return false
        }

        return true

    }

    override fun toString(): String {
        return "EnumValueDefinition{" +
                "name='" + name + '\'' +
                ", directives=" + directives +
                '}'
    }
}
