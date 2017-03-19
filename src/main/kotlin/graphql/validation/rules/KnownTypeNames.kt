package graphql.validation.rules


import graphql.language.TypeName
import graphql.validation.*
import graphql.validation.*

class KnownTypeNames(validationContext: IValidationContext,
                     validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkTypeName(typeName: TypeName) {
        if (validationContext.schema.type(typeName.name) == null) {
            val message = String.format("Unknown type %s", typeName.name)
            addError(ValidationError(ValidationErrorType.UnknownType, typeName.sourceLocation, message))
        }
    }
}
