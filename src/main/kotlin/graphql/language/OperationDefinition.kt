package graphql.language


import java.util.*

class OperationDefinition : AbstractNode, Definition {

    enum class Operation {
        QUERY, MUTATION, SUBSCRIPTION
    }

    var name: String? = null

    val operation: Operation
    var variableDefinitions: List<VariableDefinition> = mutableListOf()
    var directives: MutableList<Directive> = mutableListOf()
    var selectionSet: SelectionSet = SelectionSet()

    constructor(operation: Operation) {
        this.operation = operation
    }

    constructor(name: String,
                operation: Operation,
                variableDefinitions: List<VariableDefinition>,
                directives: List<Directive>,
                selectionSet: SelectionSet) {
        this.name = name
        this.operation = operation
        this.variableDefinitions = variableDefinitions
        this.directives.addAll(directives)
        this.selectionSet = selectionSet
    }

    constructor(name: String?,
                operation: Operation,
                variableDefinitions: List<VariableDefinition>,
                selectionSet: SelectionSet) {
        this.name = name
        this.operation = operation
        this.variableDefinitions = variableDefinitions
        this.selectionSet = selectionSet
    }

    constructor(name: String,
                operation: Operation,
                selectionSet: SelectionSet) {
        this.name = name
        this.operation = operation
        this.selectionSet = selectionSet
    }

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(variableDefinitions)
            result.addAll(directives)
            if (!selectionSet.isEmpty())
                result.add(selectionSet)
            return result
        }


    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as OperationDefinition

        if (if (name != null) name != that.name else that.name != null) return false
        return operation == that.operation

    }

    override fun toString(): String {
        return "OperationDefinition{" +
                "name='" + name + '\'' +
                ", operation=" + operation +
                ", variableDefinitions=" + variableDefinitions +
                ", directives=" + directives +
                ", selectionSet=" + selectionSet +
                '}'
    }

    fun add(directive: Directive) {
        directives.add(directive)
    }

    fun add(variable: VariableDefinition) {
        variableDefinitions += variable
    }
}
