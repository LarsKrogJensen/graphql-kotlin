package graphql.validation.rules

import graphql.language.Argument
import graphql.language.StringValue
import graphql.language.VariableReference
import graphql.schema.GraphQLArgument
import graphql.validation.IValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

import static graphql.ScalarsKt.GraphQLBigDecimal
import static graphql.ScalarsKt.GraphQLBoolean

class ArgumentsOfCorrectTypeTest extends Specification {

    ArgumentsOfCorrectType argumentsOfCorrectType
    IValidationContext validationContext = Mock(IValidationContext)
    ValidationErrorCollector errorCollector = new ValidationErrorCollector()

    def setup() {
        argumentsOfCorrectType = new ArgumentsOfCorrectType(validationContext, errorCollector)
    }

    def "valid type results in no error"() {
        given:
        def variableReference = new VariableReference("ref")
        def argumentLiteral = new Argument("arg", variableReference)
        def graphQLArgument = new GraphQLArgument("arg", null, GraphQLBigDecimal, null)
//        argumentsOfCorrectType.
        validationContext.argument >> graphQLArgument

        when:
        argumentsOfCorrectType.checkArgument(argumentLiteral)

        then:
        errorCollector.errors().isEmpty()
    }

    def "invalid type results in error"() {
        given:
        def stringValue = new StringValue("string")
        def argumentLiteral = new Argument("arg", stringValue)
        def graphQLArgument = new GraphQLArgument("arg", null, GraphQLBoolean, null)
        argumentsOfCorrectType.validationContext.getArgument() >> graphQLArgument
        when:
        argumentsOfCorrectType.checkArgument(argumentLiteral)
        then:
        errorCollector.containsValidationError(ValidationErrorType.WrongType)
        errorCollector.errors().size() == 1
        errorCollector.errors()[0].message() == "Validation error of type WrongType: argument value StringValue(value=string) has wrong type"
    }

    def "current null argument from context is no error"() {
        given:
        def stringValue = new StringValue("string")
        def argumentLiteral = new Argument("arg", stringValue)
        when:
        argumentsOfCorrectType.checkArgument(argumentLiteral)
        then:
        argumentsOfCorrectType.getErrors().isEmpty()
    }
}
