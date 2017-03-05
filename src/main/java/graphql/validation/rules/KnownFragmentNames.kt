package graphql.validation.rules


import graphql.language.FragmentSpread
import graphql.validation.*

class KnownFragmentNames(validationContext: ValidationContext,
                         validationErrorCollector: ValidationErrorCollector) : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkFragmentSpread(fragmentSpread: FragmentSpread) {
        val fragmentDefinition = validationContext.getFragment(fragmentSpread.name)
        if (fragmentDefinition == null) {
            val message = String.format("Undefined framgent %s", fragmentSpread.name)
            addError(ValidationError(ValidationErrorType.UndefinedFragment, fragmentSpread.sourceLocation, message))
        }
    }
}
