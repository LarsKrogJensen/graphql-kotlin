package graphql.language


import java.util.ArrayList

data class TypeName(var name: String) : AbstractNode(), Type {

    override val children: List<Node>
        get() = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val namedType = node as TypeName

        return name != namedType.name
    }
}
