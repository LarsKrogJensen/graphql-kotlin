package graphql.language


import java.util.ArrayList

class InputValueDefinition (val name: String,
                            var type: Type? = null,
                            defaultValue: Value? = null) : AbstractNode() {
    var defaultValue: Value? = null
        private set
    val directives: MutableList<Directive> = ArrayList()

    init {
        this.defaultValue = defaultValue
    }

    fun setValue(defaultValue: Value) {
        this.defaultValue = defaultValue
    }

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            type?.let { result.add(it) }
            defaultValue?.let { result.add(it) }
            result.addAll(directives)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as InputValueDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "InputValueDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", defaultValue=" + defaultValue +
                ", directives=" + directives +
                '}'
    }
}
