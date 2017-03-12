package graphql.validation.rules


import graphql.language.OperationDefinition
import graphql.language.VariableDefinition
import graphql.language.VariableReference
import graphql.validation.*

import java.util.ArrayList
import java.util.LinkedHashSet

class NoUnusedVariables(validationContext: IValidationContext,
                        validationErrorCollector: ValidationErrorCollector) : AbstractRule(validationContext, validationErrorCollector) {

    private val _variableDefinitions = ArrayList<VariableDefinition>()
    private val _usedVariables = LinkedHashSet<String>()

    init {
        isVisitFragmentSpreads = true
    }

    override fun leaveOperationDefinition(operationDefinition: OperationDefinition) {
        for (variableDefinition in _variableDefinitions) {
            if (!_usedVariables.contains(variableDefinition.name)) {
                val message = String.format("Unused variable %s", variableDefinition.name)
                addError(ValidationError(ValidationErrorType.UnusedVariable, variableDefinition.sourceLocation, message))
            }
        }
    }

    override fun checkOperationDefinition(operationDefinition: OperationDefinition) {
        _usedVariables.clear()
        _variableDefinitions.clear()
    }

    override fun checkVariableDefinition(variableDefinition: VariableDefinition) {
        _variableDefinitions.add(variableDefinition)
    }

    override fun checkVariable(variableReference: VariableReference) {
        _usedVariables.add(variableReference.name)
    }
}
