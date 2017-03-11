package graphql.language

class ArrayValue() : AbstractNode(), Value {
    val values: MutableList<Value> = mutableListOf()

    constructor(values: List<Value>) : this() {
        this.values.addAll(values)
    }

    override val children: List<Node>
        get() = values

    fun add(value: Value) {
        values += value
    }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }
}
