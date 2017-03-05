package graphql.validation


import graphql.language.Document
import graphql.schema.GraphQLSchema
import graphql.validation.rules.*

import java.util.ArrayList

class Validator {

    fun validateDocument(schema: GraphQLSchema, document: Document): List<ValidationError> {
        val validationContext = ValidationContext(schema, document)
        
        val validationErrorCollector = ValidationErrorCollector()
        val rules = createRules(validationContext, validationErrorCollector)
        val languageTraversal = LanguageTraversal()
        languageTraversal.traverse(document, RulesVisitor(validationContext, rules))

        return validationErrorCollector.errors()
    }

    private fun createRules(validationContext: ValidationContext, validationErrorCollector: ValidationErrorCollector): List<AbstractRule> {
        val rules = ArrayList<AbstractRule>()

        rules +=  ArgumentsOfCorrectType(validationContext, validationErrorCollector)

        rules +=  FieldsOnCorrectType(validationContext, validationErrorCollector)
        rules +=  FragmentsOnCompositeType(validationContext, validationErrorCollector)

        rules +=  KnownArgumentNames(validationContext, validationErrorCollector)
        rules +=  KnownDirectives(validationContext, validationErrorCollector)
        rules +=  KnownFragmentNames(validationContext, validationErrorCollector)
        rules +=  KnownTypeNames(validationContext, validationErrorCollector)

        rules +=  NoFragmentCycles(validationContext, validationErrorCollector)
        rules +=  NoUndefinedVariables(validationContext, validationErrorCollector)
        rules +=  NoUnusedFragments(validationContext, validationErrorCollector)
        rules +=  NoUnusedVariables(validationContext, validationErrorCollector)

        rules += OverlappingFieldsCanBeMerged(validationContext, validationErrorCollector)
        rules += PossibleFragmentSpreads(validationContext, validationErrorCollector)
        rules += ProvidedNonNullArguments(validationContext, validationErrorCollector)
        rules += ScalarLeafs(validationContext, validationErrorCollector)
        rules += VariableDefaultValuesOfCorrectType(validationContext, validationErrorCollector)
        rules += VariablesAreInputTypes(validationContext, validationErrorCollector)
        rules += VariableTypesMatchRule(validationContext, validationErrorCollector)
        rules += LoneAnonymousOperation(validationContext, validationErrorCollector)

        return rules
    }
}
