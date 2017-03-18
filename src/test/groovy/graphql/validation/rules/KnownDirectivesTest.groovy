package graphql.validation.rules

import graphql.StarWarsSchemaKt
import graphql.language.Document
import graphql.parser.Parser
import graphql.validation.*
import spock.lang.Specification

class KnownDirectivesTest extends Specification {

    IValidationContext validationContext = Mock(IValidationContext)
    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    KnownDirectives knownDirectives = new KnownDirectives(validationContext,errorCollector)

    def setup() {
        def traversalContext = Mock(ITraversalContext)
        validationContext.getSchema() >> StarWarsSchemaKt.starWarsSchema
        validationContext.getTraversalContext() >> traversalContext
    }


    def "misplaced directive"(){
        given:
        def query = """
          query Foo @include(if: true) {
                name
              }
        """

        Document document = new Parser().parseDocument(query)
        LanguageTraversal languageTraversal = new LanguageTraversal();

        when:
        languageTraversal.traverse(document, new RulesVisitor(validationContext, [knownDirectives], false));

        then:
        errorCollector.containsValidationError(ValidationErrorType.MisplacedDirective)

    }

    def "well placed directive"(){
        given:
        def query = """
          query Foo  {
                name @include(if: true)
              }
        """

        Document document = new Parser().parseDocument(query)
        LanguageTraversal languageTraversal = new LanguageTraversal();

        when:
        languageTraversal.traverse(document, new RulesVisitor(validationContext, [knownDirectives], false));

        then:
        errorCollector.errors().isEmpty()

    }




}
