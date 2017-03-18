package graphql.validation


import graphql.language.Node

class ErrorFactory {

    fun newError(validationErrorType: ValidationErrorType,
                 locations: List<Node>,
                 description: String): ValidationError {
        val locationList = locations.map(Node::sourceLocation).filterNotNull()
        return ValidationError(validationErrorType, locationList, description)
    }

}
