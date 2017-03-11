package graphql.language


import java.util.ArrayList

class ObjectValue : AbstractNode(), Value {
    val objectFields = ArrayList<ObjectField>()

    override val children: List<Node>
        get() = objectFields

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }

    override fun toString(): String {
        return "ObjectValue{" +
                "objectFields=" + objectFields +
                '}'
    }

    fun add(objectField: ObjectField) {
        objectFields += objectField
    }
}
