package graphql.validation.rules


import graphql.introspection.Introspection.DirectiveLocation
import graphql.language.*
import graphql.language.OperationDefinition.Operation
import graphql.schema.GraphQLDirective
import graphql.validation.*

class KnownDirectives(validationContext: IValidationContext,
                      validationErrorCollector: ValidationErrorCollector) : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkDirective(directive: Directive, ancestors: List<Node>) {
        val graphQLDirective = validationContext.schema.directive(directive.name)
        if (graphQLDirective == null) {
            val message = String.format("Unknown directive %s", directive.name)
            addError(ValidationError(ValidationErrorType.UnknownDirective, directive.sourceLocation, message))
            return
        }

        val ancestor = ancestors[ancestors.size - 1]
        if (hasInvalidLocation(graphQLDirective, ancestor)) {
            val message = String.format("Directive %s not allowed here", directive.name)
            addError(ValidationError(ValidationErrorType.MisplacedDirective, directive.sourceLocation, message))
        }
    }

    private fun hasInvalidLocation(directive: GraphQLDirective, ancestor: Node): Boolean {
        if (ancestor is OperationDefinition) {
            val operation = ancestor.operation
            return if (Operation.QUERY == operation)
                !directive.locations.contains(DirectiveLocation.QUERY)
            else
                !directive.locations.contains(DirectiveLocation.MUTATION)
        } else if (ancestor is Field) {
            return !directive.locations.contains(DirectiveLocation.FIELD)
        } else if (ancestor is FragmentSpread) {
            return !directive.locations.contains(DirectiveLocation.FRAGMENT_SPREAD)
        } else if (ancestor is FragmentDefinition) {
            return !directive.locations.contains(DirectiveLocation.FRAGMENT_DEFINITION)
        } else if (ancestor is InlineFragment) {
            return !directive.locations.contains(DirectiveLocation.INLINE_FRAGMENT)
        }
        return true
    }
}
