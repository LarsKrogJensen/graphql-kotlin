package graphql.language


import java.util.ArrayList

class DirectiveDefinition(private val name: String) : AbstractNode(), Definition {
    val inputValueDefinitions: MutableList<InputValueDefinition> = ArrayList()
    val directiveLocations: MutableList<DirectiveLocation> = ArrayList()

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(inputValueDefinitions)
            result.addAll(directiveLocations)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as DirectiveDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "DirectiveDefinition{" +
                "name='" + name + "'" +
                ", inputValueDefinitions=" + inputValueDefinitions +
                ", directiveLocations=" + directiveLocations +
                "}"
    }
}
