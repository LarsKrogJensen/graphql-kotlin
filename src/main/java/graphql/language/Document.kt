package graphql.language

class Document : AbstractNode {
    var definitions: List<Definition> = mutableListOf()

    constructor()

    constructor(definitions: List<Definition>) {
        this.definitions = definitions
    }


    override val children: List<Node>
        get() = definitions


    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }

    override fun toString(): String {
        return "Document{" +
                "definitions=" + definitions +
                '}'
    }

    fun add(definition: Definition) {
        definitions += definition
    }

}
