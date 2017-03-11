package graphql.language


import java.util.ArrayList

class InterfaceTypeDefinition(override val name: String) : AbstractNode(), TypeDefinition {
    val fieldDefinitions: MutableList<FieldDefinition> = ArrayList()
    val directives: MutableList<Directive> = ArrayList()

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(fieldDefinitions)
            result.addAll(directives)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as InterfaceTypeDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "InterfaceTypeDefinition{" +
                "name='" + name + '\'' +
                ", fieldDefinitions=" + fieldDefinitions +
                ", directives=" + directives +
                '}'
    }
}
