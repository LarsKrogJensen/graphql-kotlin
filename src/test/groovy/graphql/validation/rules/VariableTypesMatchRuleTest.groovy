package graphql.validation.rules

import graphql.ScalarsKt
import graphql.StarWarsSchema
import graphql.language.*
import graphql.validation.IValidationContext
import graphql.validation.ValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

class VariableTypesMatchRuleTest extends Specification {

    IValidationContext validationContext = Mock(IValidationContext)
    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    VariableTypesMatchRule variableTypesMatchRule = new VariableTypesMatchRule(validationContext,errorCollector)

    def setup(){
    }

    def "invalid type"(){
        given:
        def defaultValue = new StringValue("default")
        def astType = new TypeName("String")
        def expectedType = ScalarsKt.GraphQLBoolean

        validationContext.getSchema() >> StarWarsSchema.starWarsSchema
        validationContext.getInputType() >> expectedType
        variableTypesMatchRule.variablesTypesMatcher
                .doesVariableTypesMatch(ScalarsKt.GraphQLString, defaultValue, expectedType) >> false

        when:
        variableTypesMatchRule.checkOperationDefinition(new OperationDefinition(OperationDefinition.Operation.QUERY))
        variableTypesMatchRule.checkVariableDefinition(new VariableDefinition("var",astType,defaultValue))
        variableTypesMatchRule.checkVariable(new VariableReference("var"))

        then:
        errorCollector.containsValidationError(ValidationErrorType.VariableTypeMismatch)


    }
}
