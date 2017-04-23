package graphql.schema.validation

import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLSchema

class Validator {

    private val _processed = mutableSetOf<GraphQLOutputType>()

    fun validateSchema(schema: GraphQLSchema): Set<ValidationError> {
        val validationErrorCollector = ValidationErrorCollector()
        val rules = mutableListOf<ValidationRule>(NoUnbrokenInputCycles())

        traverse(schema.queryType, rules, validationErrorCollector)
        if (schema.isSupportingMutations) {
            traverse(schema.mutationType!!, rules, validationErrorCollector)
        }
        if (schema.isSupportingSubscriptions) {
            traverse(schema.subscriptionType!!, rules, validationErrorCollector)
        }
        return validationErrorCollector.errors()
    }

    private fun traverse(root: GraphQLOutputType,
                         rules: List<ValidationRule>,
                         validationErrorCollector: ValidationErrorCollector) {
        if (_processed.contains(root)) {
            return
        }
        _processed.add(root)
        if (root is GraphQLFieldsContainer) {
            for (fieldDefinition in root.fieldDefinitions) {
                for (rule in rules) {
                    rule.check(fieldDefinition, validationErrorCollector)
                }
                traverse(fieldDefinition.type, rules, validationErrorCollector)
            }
        }
    }
}
