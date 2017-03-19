package graphql.validation


import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

import java.util.ArrayList

class ValidationError(val validationErrorType: ValidationErrorType) : GraphQLError {

    private val _sourceLocations = ArrayList<SourceLocation>()
    private var _description: String? = null

    constructor(validationErrorType: ValidationErrorType,
                sourceLocation: SourceLocation? = null,
                description: String? = null) : this(validationErrorType) {
        if (sourceLocation != null) {
            _sourceLocations.add(sourceLocation)
        }
        _description = description
    }

    constructor(validationErrorType: ValidationErrorType,
                sourceLocations: List<SourceLocation>,
                description: String? = null) : this(validationErrorType) {
        _sourceLocations.addAll(sourceLocations)
        _description = description
    }

    override fun message(): String {
        return String.format("Validation error of type %s: %s", validationErrorType, _description)
    }

    override fun locations(): List<SourceLocation> {
        return _sourceLocations
    }

    override fun errorType(): ErrorType {
        return ErrorType.ValidationError
    }


    override fun toString(): String {
        return "ValidationError{" +
                "validationErrorType=" + validationErrorType +
                ", sourceLocations=" + _sourceLocations +
                ", description='" + _description + '\'' +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as ValidationError

        return GraphQLError.Helper.equals(this, that)
    }

    override fun hashCode(): Int {
        return GraphQLError.Helper.hashCode(this)
    }
}
