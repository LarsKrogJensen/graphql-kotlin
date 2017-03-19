package graphql.validation.rules

import graphql.language.Document
import graphql.language.OperationDefinition
import graphql.validation.IValidationContext
import graphql.validation.ValidationErrorType
import graphql.validation.*

class LoneAnonymousOperation(validationContext: IValidationContext,
                             validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    private var _hasAnonymousOp = false
    private var _count = 0

    override fun checkOperationDefinition(operationDefinition: OperationDefinition) {
        super.checkOperationDefinition(operationDefinition)
        val name = operationDefinition.name
        var message: String? = null

        if (name == null) {
            _hasAnonymousOp = true
            if (_count > 0) {
                message = "Anonymous operation with other operations."
            }
        } else {
            if (_hasAnonymousOp) {
                message = "Operation $name is following anonymous operation."
            }
        }
        _count++
        if (message != null) {
            addError(ValidationError(ValidationErrorType.LoneAnonymousOperationViolation, operationDefinition.sourceLocation, message))
        }
    }

    override fun documentFinished(document: Document) {
        super.documentFinished(document)
        _hasAnonymousOp = false
    }
}
