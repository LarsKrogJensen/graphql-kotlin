package graphql.language


import java.util.ArrayList

class UnionTypeDefinition(override val name: String) : AbstractNode(), TypeDefinition {
    val directives: MutableList<Directive> = ArrayList()
    val memberTypes: MutableList<Type> = ArrayList()

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(directives)
            result.addAll(memberTypes)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as UnionTypeDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "UnionTypeDefinition{" +
                "name='" + name + '\'' +
                "directives=" + directives +
                ", memberTypes=" + memberTypes +
                '}'
    }
}
