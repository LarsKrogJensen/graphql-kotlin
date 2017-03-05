package graphql.schema.validation

import java.util.LinkedHashSet

class ValidationErrorCollector {

    private val _errors = LinkedHashSet<ValidationError>()

    fun addError(validationError: ValidationError) {
        _errors.add(validationError)
    }

    fun errors(): Set<ValidationError> {
        return _errors
    }

    fun containsValidationError(validationErrorType: ValidationErrorType): Boolean {
        for ((errorType) in _errors) {
            if (errorType === validationErrorType) return true
        }
        return false
    }
}
