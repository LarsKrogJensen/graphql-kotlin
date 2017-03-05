package graphql.validation


import graphql.language.Node
import graphql.language.SourceLocation

import java.util.ArrayList

class ErrorFactory {

    fun newError(validationErrorType: ValidationErrorType,
                 locations: List<Node>,
                 description: String): ValidationError {
        val locationList = locations.map { it.sourceLocation }
        return ValidationError(validationErrorType, locationList, description)
    }

    fun newError(validationErrorType: ValidationErrorType, description: String): ValidationError {
        return ValidationError(validationErrorType, emptyList(), description)
    }
}
