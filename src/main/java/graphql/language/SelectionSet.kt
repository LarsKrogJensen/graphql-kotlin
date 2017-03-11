package graphql.language

class SelectionSet : AbstractNode {

    private val selections = mutableListOf<Selection>()

    fun selections(): MutableList<Selection> {
        return selections
    }

    constructor()

    constructor(selections: List<Selection>) {
        this.selections.addAll(selections)
    }

    fun isEmpty() = selections.isEmpty()

    override val children: List<Node>
        get() = selections

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        return true
    }

    override fun toString(): String {
        return "SelectionSet{" +
                "selections=" + selections +
                '}'
    }
}
