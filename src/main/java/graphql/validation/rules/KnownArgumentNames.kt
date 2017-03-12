package graphql.validation.rules

import graphql.language.Argument
import graphql.validation.*


class KnownArgumentNames(validationContext: IValidationContext,
                         validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {
    override fun checkArgument(argument: Argument) {
        val directiveDef = validationContext.directive
        if (directiveDef != null) {
            val directiveArgument = directiveDef.argument(argument.name)
            if (directiveArgument == null) {
                val message = String.format("Unknown directive argument %s", argument.name)
                addError(ValidationError(ValidationErrorType.UnknownDirective, argument.sourceLocation, message))
            }

            return
        }

        val fieldDef = validationContext.fieldDef ?: return
        val fieldArgument = fieldDef.getArgument(argument.name)
        if (fieldArgument == null) {
            val message = String.format("Unknown field argument %s", argument.name)
            addError(ValidationError(ValidationErrorType.UnknownArgument, argument.sourceLocation, message))
        }
    }
}
