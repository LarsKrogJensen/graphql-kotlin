package graphql.language


import java.util.ArrayList

class OperationDefinition : AbstractNode, Definition {

    enum class Operation {
        QUERY, MUTATION
    }

    var name: String? = null

    var operation: Operation? = null
    var variableDefinitions: List<VariableDefinition> = mutableListOf()
    var directives: List<Directive> = mutableListOf()
    var selectionSet: SelectionSet? = null

    constructor()

    constructor(name: String, operation: Operation, variableDefinitions: List<VariableDefinition>, directives: List<Directive>, selectionSet: SelectionSet) {
        this.name = name
        this.operation = operation
        this.variableDefinitions = variableDefinitions
        this.directives = directives
        this.selectionSet = selectionSet
    }

    constructor(name: String, operation: Operation, variableDefinitions: List<VariableDefinition>, selectionSet: SelectionSet) {
        this.name = name
        this.operation = operation
        this.variableDefinitions = variableDefinitions
        this.selectionSet = selectionSet
    }

    constructor(name: String, operation: Operation, selectionSet: SelectionSet) {
        this.name = name
        this.operation = operation
        this.selectionSet = selectionSet
    }

    override val children: List<Node>
        get() {
            val result = ArrayList<Node>()
            result.addAll(variableDefinitions)
            result.addAll(directives)
            selectionSet?.let { result.add(it) }
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
        directives += directive
    }

    fun add(variable: VariableDefinition) {
        variableDefinitions += variable
    }
}
