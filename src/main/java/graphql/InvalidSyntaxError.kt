package graphql


import graphql.language.SourceLocation

import java.util.ArrayList

class InvalidSyntaxError(sourceLocation: SourceLocation?) : GraphQLError {

    private val _sourceLocations = ArrayList<SourceLocation>()

    init {
        if (sourceLocation != null)
            this._sourceLocations.add(sourceLocation)
    }

    override fun message(): String? {
        return "Invalid Syntax"
    }

    override fun locations(): List<SourceLocation>? {
        return _sourceLocations
    }

    override fun errorType(): ErrorType {
        return ErrorType.InvalidSyntax
    }

    override fun toString(): String {
        return "InvalidSyntaxError{" +
                "sourceLocations=" + _sourceLocations +
                '}'
    }
}
