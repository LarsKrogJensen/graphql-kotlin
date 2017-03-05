package graphql.schema.validation

data class ValidationError(val errorType: ValidationErrorType,
                           val description: String)
