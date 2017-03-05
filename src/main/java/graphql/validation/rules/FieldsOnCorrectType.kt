package graphql.validation.rules


import graphql.language.Field
import graphql.validation.*

class FieldsOnCorrectType(validationContext: ValidationContext,
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
