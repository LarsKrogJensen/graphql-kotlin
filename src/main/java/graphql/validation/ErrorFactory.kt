package graphql.validation


import graphql.language.Node

class ErrorFactory {

    fun newError(validationErrorType: ValidationErrorType,
                 locations: List<Node>,
                 description: String): ValidationError {
        val locationList = locations.map { it.sourceLocation }.filterNotNull()
        return ValidationError(validationErrorType, locationList, description)
    }

    fun newError(validationErrorType: ValidationErrorType, description: String): ValidationError {
        return ValidationError(validationErrorType, emptyList(), description)
    }
}
