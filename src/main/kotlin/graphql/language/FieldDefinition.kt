package graphql.language


import java.util.ArrayList

class FieldDefinition : AbstractNode {
    val name: String
    var type: Type? = null
    val inputValueDefinitions: MutableList<InputValueDefinition> = ArrayList()
    val directives: MutableList<Directive> = ArrayList()

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, type: Type) {
        this.name = name
        this.type = type
    }

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            type?.let { result.add(it) }
            result.addAll(inputValueDefinitions)
            result.addAll(directives)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as FieldDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "FieldDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", inputValueDefinitions=" + inputValueDefinitions +
                ", directives=" + directives +
                '}'
    }
}
