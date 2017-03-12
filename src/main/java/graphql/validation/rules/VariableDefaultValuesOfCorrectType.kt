package graphql.validation.rules

import graphql.language.VariableDefinition
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLNonNull
import graphql.validation.*


class VariableDefaultValuesOfCorrectType(validationContext: IValidationContext,
                                         validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {


    override fun checkVariableDefinition(variableDefinition: VariableDefinition) {
        val inputType = validationContext.inputType ?: return
        if (inputType is GraphQLNonNull && variableDefinition.defaultValue != null) {
            val message = "Missing value for non null type"
            addError(ValidationError(ValidationErrorType.DefaultForNonNullArgument, variableDefinition.sourceLocation, message))
        }
        if (variableDefinition.defaultValue != null && !validationUtil.isValidLiteralValue(variableDefinition.defaultValue, inputType)) {
            val message = String.format("Bad default value %s for type %s", variableDefinition.defaultValue, inputType.name)
            addError(ValidationError(ValidationErrorType.BadValueForDefaultArg, variableDefinition.sourceLocation, message))
        }
    }
}
