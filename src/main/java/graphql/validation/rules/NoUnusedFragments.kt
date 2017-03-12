package graphql.validation.rules


import graphql.language.Document
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.OperationDefinition
import graphql.validation.*

import java.util.ArrayList
import java.util.LinkedHashMap

class NoUnusedFragments(validationContext: IValidationContext,
                        validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    private val _allDeclaredFragments = ArrayList<FragmentDefinition>()
    private var _usedFragments: MutableList<String> = ArrayList()
    private val _spreadsInDefinition = LinkedHashMap<String, List<String>>()
    private val _fragmentsUsedDirectlyInOperation = ArrayList<List<String>>()

    override fun checkOperationDefinition(operationDefinition: OperationDefinition) {
        _usedFragments = ArrayList<String>()
        _fragmentsUsedDirectlyInOperation.add(_usedFragments)
    }

    override fun checkFragmentSpread(fragmentSpread: FragmentSpread) {
        _usedFragments.add(fragmentSpread.name)
    }

    override fun checkFragmentDefinition(fragmentDefinition: FragmentDefinition) {
        _allDeclaredFragments.add(fragmentDefinition)
        _usedFragments = ArrayList<String>()
        _spreadsInDefinition.put(fragmentDefinition.name, _usedFragments)
    }

    override fun documentFinished(document: Document) {

        val allUsedFragments = ArrayList<String>()
        for (fragmentsInOneOperation in _fragmentsUsedDirectlyInOperation) {
            for (fragment in fragmentsInOneOperation) {
                collectUsedFragmentsInDefinition(allUsedFragments, fragment)
            }
        }

        for (fragmentDefinition in _allDeclaredFragments) {
            if (!allUsedFragments.contains(fragmentDefinition.name)) {
                val message = String.format("Unused fragment %s", fragmentDefinition.name)
                addError(ValidationError(ValidationErrorType.UnusedFragment, fragmentDefinition.sourceLocation, message))
            }
        }
    }

    private fun collectUsedFragmentsInDefinition(result: MutableList<String>, fragmentName: String) {
        if (result.contains(fragmentName)) return
        result.add(fragmentName)
        val spreadList = _spreadsInDefinition[fragmentName]
        if (spreadList != null) {
            for (fragment in spreadList) {
                collectUsedFragmentsInDefinition(result, fragment)
            }
        }

    }

}