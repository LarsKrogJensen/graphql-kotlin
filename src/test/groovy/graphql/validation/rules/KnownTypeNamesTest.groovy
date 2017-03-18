package graphql.validation.rules

import graphql.StarWarsSchemaKt
import graphql.language.TypeName
import graphql.validation.IValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

class KnownTypeNamesTest extends Specification {

    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    IValidationContext validationContext = Mock(IValidationContext)
    KnownTypeNames knownTypeNames = new KnownTypeNames(validationContext, errorCollector)

    def "unknown types is an error"() {
        given:
        knownTypeNames.validationContext.getSchema() >> StarWarsSchemaKt.starWarsSchema

        when:
        knownTypeNames.checkTypeName(new TypeName("Simpson"))

        then:
        errorCollector.containsValidationError(ValidationErrorType.UnknownType)

    }
}
