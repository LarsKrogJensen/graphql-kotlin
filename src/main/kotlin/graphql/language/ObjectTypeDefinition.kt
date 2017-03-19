package graphql.language


import java.util.ArrayList

open class ObjectTypeDefinition(override var name: String) : AbstractNode(), TypeDefinition {
    val implements: MutableList<Type> = ArrayList()
    val directives: MutableList<Directive> = ArrayList()
    val fieldDefinitions: MutableList<FieldDefinition> = ArrayList()

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(implements)
            result.addAll(directives)
            result.addAll(fieldDefinitions)
            return result
        }

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as ObjectTypeDefinition

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "ObjectTypeDefinition{" +
                "name='" + name + '\'' +
                ", implements=" + implements +
                ", directives=" + directives +
                ", fieldDefinitions=" + fieldDefinitions +
                '}'
    }
}
