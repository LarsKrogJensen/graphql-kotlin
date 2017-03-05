package graphql.validation.rules


import graphql.language.TypeName
import graphql.language.VariableDefinition
import graphql.schema.GraphQLType
import graphql.schema.SchemaUtil
import graphql.validation.*

class VariablesAreInputTypes(validationContext: ValidationContext,
                             validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    private val schemaUtil = SchemaUtil()

    override fun checkVariableDefinition(variableDefinition: VariableDefinition) {
        val (name) = validationUtil.getUnmodifiedType(variableDefinition.type)

        val type = validationContext.schema.type(name) ?: return
        if (!schemaUtil.isInputType(type)) {
            val message = "Wrong type for a variable"
            addError(ValidationError(ValidationErrorType.NonInputTypeOnVariable, variableDefinition.sourceLocation, message))
        }
    }
}
