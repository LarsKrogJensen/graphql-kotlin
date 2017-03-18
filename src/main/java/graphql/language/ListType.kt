package graphql.language


import java.util.ArrayList

class ListType : AbstractNode, Type {

    var type: Type? = null

    constructor()

    constructor(type: Type) {
        this.type = type
    }

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            type?.let { result.add(it) }
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }


    override fun toString(): String {
        return "ListType{" +
                "type=" + type +
                '}'
    }
}
