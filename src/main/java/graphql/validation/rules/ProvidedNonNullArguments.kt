package graphql.validation.rules


import graphql.language.Argument
import graphql.language.Directive
import graphql.language.Field
import graphql.language.Node
import graphql.schema.GraphQLNonNull
import graphql.validation.*
import java.util.*

class ProvidedNonNullArguments(validationContext: ValidationContext,
                               validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    override fun checkField(field: Field) {
        val fieldDef = validationContext.fieldDef ?: return
        val argumentMap = argumentMap(field.arguments)

        for (graphQLArgument in fieldDef.arguments) {
            val argument = argumentMap[graphQLArgument.name]
            if (argument == null && graphQLArgument.type is GraphQLNonNull) {
                val message = String.format("Missing field argument %s", graphQLArgument.name)
                addError(ValidationError(ValidationErrorType.MissingFieldArgument, field.sourceLocation, message))
            }
        }
    }

    private fun argumentMap(arguments: List<Argument>): Map<String, Argument> {
        val result = LinkedHashMap<String, Argument>()
        for (argument in arguments) {
            result.put(argument.name, argument)
        }
        return result
    }


    override fun checkDirective(directive: Directive, ancestors: List<Node>) {
        val graphQLDirective = validationContext.directive ?: return
        val argumentMap = argumentMap(directive.arguments)

        for (graphQLArgument in graphQLDirective.arguments) {
            val argument = argumentMap[graphQLArgument.name]
            if (argument == null && graphQLArgument.type is GraphQLNonNull) {
                val message = String.format("Missing directive argument %s", graphQLArgument.name)
                addError(ValidationError(ValidationErrorType.MissingDirectiveArgument, directive.sourceLocation, message))
            }
        }
    }
}
