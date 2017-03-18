package graphql.validation.rules


import graphql.language.FragmentDefinition
import graphql.language.InlineFragment
import graphql.schema.GraphQLCompositeType
import graphql.validation.*

class FragmentsOnCompositeType(validationContext: IValidationContext,
                               validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkInlineFragment(inlineFragment: InlineFragment) {
        val typeCondition = inlineFragment.typeCondition ?: return

        val type = validationContext.schema.type(typeCondition.name) ?: return
        if (type !is GraphQLCompositeType) {
            val message = "Inline fragment type condition is invalid, must be on Object/Interface/Union"
            addError(ValidationError(ValidationErrorType.InlineFragmentTypeConditionInvalid, inlineFragment.sourceLocation, message))
        }
    }

    override fun checkFragmentDefinition(fragmentDefinition: FragmentDefinition) {
        val typeCondition = fragmentDefinition.typeCondition

        val type = validationContext.schema.type(typeCondition.name) ?: return
        if (type !is GraphQLCompositeType) {
            val message = "Fragment type condition is invalid, must be on Object/Interface/Union"
            addError(ValidationError(ValidationErrorType.InlineFragmentTypeConditionInvalid, fragmentDefinition.sourceLocation, message))
        }
    }
}
