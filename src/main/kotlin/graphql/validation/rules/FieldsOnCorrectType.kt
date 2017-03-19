package graphql.validation.rules


import graphql.language.Field
import graphql.validation.IValidationContext
import graphql.validation.ValidationError
import graphql.validation.ValidationErrorType
import graphql.validation.*

class FieldsOnCorrectType(validationContext: IValidationContext,
                          validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkField(field: Field) {
        if (validationContext.parentType == null)
            return

        val fieldDef = validationContext.fieldDef
        if (fieldDef == null) {
            val message = String.format("Field %s is undefined", field.name)
            addError(ValidationError(ValidationErrorType.FieldUndefined, field.sourceLocation, message))
        }

    }
}
