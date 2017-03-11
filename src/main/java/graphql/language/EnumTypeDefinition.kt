package graphql.language

import java.util.ArrayList

class EnumTypeDefinition (override val name: String,
                          directives: List<Directive>? = null) : AbstractNode(), TypeDefinition {
    val enumValueDefinitions: MutableList<EnumValueDefinition>
    val directives: MutableList<Directive> = mutableListOf<Directive>()

    init {
        if (directives != null)
            this.directives.addAll(directives)
        this.enumValueDefinitions = ArrayList<EnumValueDefinition>()
    }


    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(enumValueDefinitions)
            result.addAll(directives)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as EnumTypeDefinition

        if (name != that.name) {
            return false
        }
        return true

    }

    override fun toString(): String {
        return "EnumTypeDefinition{" +
                "name='" + name + '\'' +
                ", enumValueDefinitions=" + enumValueDefinitions +
                ", directives=" + directives +
                '}'
    }
}
