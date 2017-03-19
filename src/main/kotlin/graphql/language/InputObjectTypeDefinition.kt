package graphql.language


import java.util.ArrayList

class InputObjectTypeDefinition(override val name: String) : AbstractNode(), TypeDefinition {
    val directives: MutableList<Directive> = ArrayList()
    val inputValueDefinitions: MutableList<InputValueDefinition> = ArrayList()



    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(directives)
            result.addAll(inputValueDefinitions)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as InputObjectTypeDefinition

        if (name != that.name) {
            return false
        }
        return true

    }


    override fun toString(): String {
        return "InputObjectTypeDefinition{" +
                "name='" + name + '\'' +
                ", directives=" + directives +
                ", inputValueDefinitions=" + inputValueDefinitions +
                '}'
    }
}
