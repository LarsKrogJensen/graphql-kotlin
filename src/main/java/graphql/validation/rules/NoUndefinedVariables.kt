package graphql.validation.rules


import graphql.language.FragmentDefinition
import graphql.language.OperationDefinition
import graphql.language.VariableDefinition
import graphql.language.VariableReference
import graphql.validation.*

import java.util.LinkedHashSet

class NoUndefinedVariables(validationContext: ValidationContext,
                           validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    private val _variableNames = LinkedHashSet<String>()

    init {
        isVisitFragmentSpreads = true
    }

    override fun checkOperationDefinition(operationDefinition: OperationDefinition) {
        _variableNames.clear()
    }

    override fun checkFragmentDefinition(fragmentDefinition: FragmentDefinition) {
        super.checkFragmentDefinition(fragmentDefinition)
    }

    override fun checkVariable(variableReference: VariableReference) {
        if (!_variableNames.contains(variableReference.name)) {
            val message = String.format("Undefined variable %s", variableReference.name)
            addError(ValidationError(ValidationErrorType.UndefinedVariable, variableReference.sourceLocation, message))
        }
    }

    override fun checkVariableDefinition(variableDefinition: VariableDefinition) {
        _variableNames.add(variableDefinition.name)
    }
}
