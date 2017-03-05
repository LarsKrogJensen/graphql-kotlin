package graphql.validation


import java.util.ArrayList

class ValidationErrorCollector {

    private val _errors = ArrayList<ValidationError>()

    fun addError(validationError: ValidationError) {
        _errors.add(validationError)
    }

    fun errors(): List<ValidationError> {
        return _errors
    }

    fun containsValidationError(validationErrorType: ValidationErrorType): Boolean {
        return _errors.any { it.validationErrorType == validationErrorType }
    }
}
