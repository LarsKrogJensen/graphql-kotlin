package graphql.schema.validation

import graphql.schema.GraphQLFieldDefinition

interface ValidationRule {

    fun check(fieldDef: GraphQLFieldDefinition<*>, validationErrorCollector: ValidationErrorCollector)
}
