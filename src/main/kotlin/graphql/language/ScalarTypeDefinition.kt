package graphql.language


import java.util.ArrayList

class ScalarTypeDefinition(override val name: String) : AbstractNode(), TypeDefinition {
    val directives: MutableList<Directive> = ArrayList()

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(directives)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as ScalarTypeDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "ScalarTypeDefinition{" +
                "name='" + name + '\'' +
                ", directives=" + directives +
                '}'
    }
}
