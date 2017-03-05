package graphql.language

class VariableDefinition : AbstractNode {
    val name: String
    var type: Type? = null
    var defaultValue: Value? = null


    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, type: Type) : this(name){
        this.type = type
    }

    constructor(name: String, type: Type, defaultValue: Value) : this(name, type){
        this.defaultValue = defaultValue
    }

    override val children: List<Node>
        get() {
            val result = mutableListOf<Node>()
            type?.let { result.add(it) }
            defaultValue?.let { result.add(it) }
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as VariableDefinition

        return name == that.name
    }


    override fun toString(): String {
        return "VariableDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", defaultValue=" + defaultValue +
                '}'
    }
}
