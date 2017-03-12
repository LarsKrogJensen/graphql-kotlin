package graphql.validation.rules

import graphql.language.FragmentSpread
import graphql.validation.IValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

class KnownFragmentNamesTest extends Specification {

    IValidationContext validationContext = Mock(IValidationContext)
    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    KnownFragmentNames knownFragmentNames = new KnownFragmentNames(validationContext, errorCollector)

    def "unknown fragment reference in fragment spread"() {
        given:
        FragmentSpread fragmentSpread = new FragmentSpread("fragment")
        knownFragmentNames.validationContext.fragment("fragment") >> null
        when:
        knownFragmentNames.checkFragmentSpread(fragmentSpread);

        then:
        errorCollector.containsValidationError(ValidationErrorType.UndefinedFragment)

    }


}
