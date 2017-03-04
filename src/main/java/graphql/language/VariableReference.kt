package graphql.language


import java.util.ArrayList

data class VariableReference(var name: String?) : AbstractNode(), Value {

    override val children: List<Node>
        get() = ArrayList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as VariableReference

        return !if (name != null) name != that.name else that.name != null

    }
}
