package graphql.language


import java.util.ArrayList

class SchemaDefinition : AbstractNode(), Definition {
    val directives: MutableList<Directive> = ArrayList()
    val operationTypeDefinitions: MutableList<OperationTypeDefinition> = ArrayList()

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(directives)
            result.addAll(operationTypeDefinitions)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }


    override fun toString(): String {
        return "SchemaDefinition{" +
                "directives=" + directives +
                ", operationTypeDefinitions=" + operationTypeDefinitions +
                "}"
    }
}
