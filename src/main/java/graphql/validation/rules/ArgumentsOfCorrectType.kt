package graphql.validation.rules


import graphql.language.Argument
import graphql.schema.GraphQLArgument
import graphql.validation.*

class ArgumentsOfCorrectType(validationContext: IValidationContext,
                             validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkArgument(argument: Argument) {
        val fieldArgument = validationContext.argument ?: return
        if (!validationUtil.isValidLiteralValue(argument.value, fieldArgument.type)) {
            val message = String.format("argument value %s has wrong type", argument.value)
            addError(ValidationError(ValidationErrorType.WrongType, argument.sourceLocation, message))
        }
    }
}
