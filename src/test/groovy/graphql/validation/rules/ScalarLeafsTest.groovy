package graphql.validation.rules

import graphql.ScalarsKt
import graphql.language.Field
import graphql.language.SelectionSet
import graphql.schema.GraphQLObjectType
import graphql.validation.IValidationContext
import graphql.validation.ValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

class ScalarLeafsTest extends Specification {

    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    IValidationContext validationContext = Mock(IValidationContext)
    ScalarLeafs scalarLeafs = new ScalarLeafs(validationContext, errorCollector)

    def "sub selection not allowed"() {
        given:
        Field field = new Field("hello", new SelectionSet([new Field("world")]))
        validationContext.getOutputType() >> ScalarsKt.GraphQLString
        when:
        scalarLeafs.checkField(field)

        then:
        errorCollector.containsValidationError(ValidationErrorType.SubSelectionNotAllowed)
    }

    def "sub selection required"() {
        given:
        Field field = new Field("hello")
        validationContext.getOutputType() >> GraphQLObjectType.newObject().name("objectType").build()
        when:
        scalarLeafs.checkField(field)

        then:
        errorCollector.containsValidationError(ValidationErrorType.SubSelectionRequired)
    }
}
