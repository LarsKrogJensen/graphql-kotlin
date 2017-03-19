package graphql.validation.rules

import graphql.StarWarsSchemaKt
import graphql.language.ListType
import graphql.language.NonNullType
import graphql.language.TypeName
import graphql.language.VariableDefinition
import graphql.validation.IValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

class VariablesAreInputTypesTest extends Specification {

    IValidationContext validationContext = Mock(IValidationContext)
    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    VariablesAreInputTypes variablesAreInputTypes = new VariablesAreInputTypes(validationContext, errorCollector)


    def "the unmodified ast type is not a schema input type"() {
        given:
        def astType = new NonNullType(new ListType(new TypeName(StarWarsSchemaKt.droidType.getName())))
        VariableDefinition variableDefinition = new VariableDefinition("var", astType)
        validationContext.getSchema() >> StarWarsSchemaKt.starWarsSchema

        when:
        variablesAreInputTypes.checkVariableDefinition(variableDefinition)

        then:
        errorCollector.containsValidationError(ValidationErrorType.NonInputTypeOnVariable)
    }
}
