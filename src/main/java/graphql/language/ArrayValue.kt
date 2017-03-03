package graphql.language

class ArrayValue() : AbstractNode(), Value {
    var values: List<Value> = mutableListOf()

    constructor(values: List<Value>) : this() {
        this.values = values
    }

    override val children: List<Node>
        get() = values

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }
}
