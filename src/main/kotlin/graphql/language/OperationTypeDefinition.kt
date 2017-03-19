package graphql.language


import java.util.ArrayList

class OperationTypeDefinition (val name: String, var type: Type? = null) : AbstractNode() {

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            type?.let { result.add(it) }
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as OperationTypeDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "OperationTypeDefinition{" +
                "name='" + name + "'" +
                ", type=" + type +
                "}"
    }
}
