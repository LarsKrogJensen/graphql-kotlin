package graphql.language


import java.util.ArrayList

class ObjectValue : AbstractNode, Value {
    private val objectFields = ArrayList<ObjectField>()

    constructor()

    constructor(objectFields: List<ObjectField>) {
        this.objectFields.addAll(objectFields)
    }

    fun objectFields(): List<ObjectField> {
        return objectFields
    }

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
}
