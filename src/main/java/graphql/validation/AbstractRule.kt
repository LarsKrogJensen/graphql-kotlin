package graphql.validation


import graphql.language.*

open class AbstractRule(val validationContext: IValidationContext,
                        private val validationErrorCollector: ValidationErrorCollector) {

    var isVisitFragmentSpreads: Boolean = false

    var validationUtil = ValidationUtil()

    fun addError(error: ValidationError) {
        validationErrorCollector.addError(error)
    }

    val errors: List<ValidationError>
        get() = validationErrorCollector.errors()

    open fun checkArgument(argument: Argument) {

    }

    open fun checkTypeName(typeName: TypeName) {

    }

    open fun checkVariableDefinition(variableDefinition: VariableDefinition) {

    }

    open fun checkField(field: Field) {

    }

    open fun checkInlineFragment(inlineFragment: InlineFragment) {

    }

    open fun checkDirective(directive: Directive, ancestors: List<Node>) {

    }

    open fun checkFragmentSpread(fragmentSpread: FragmentSpread) {

    }

    open fun checkFragmentDefinition(fragmentDefinition: FragmentDefinition) {

    }

    open fun checkOperationDefinition(operationDefinition: OperationDefinition) {

    }

    open fun leaveOperationDefinition(operationDefinition: OperationDefinition) {

    }

    fun checkSelectionSet(selectionSet: SelectionSet) {

    }

    open fun leaveSelectionSet(selectionSet: SelectionSet) {

    }

    open fun checkVariable(variableReference: VariableReference) {

    }

    open fun documentFinished(document: Document) {

    }
}
