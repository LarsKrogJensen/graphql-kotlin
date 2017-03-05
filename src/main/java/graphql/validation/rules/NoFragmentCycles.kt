package graphql.validation.rules


import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.Node
import graphql.validation.*
import java.util.*

class NoFragmentCycles(validationContext: ValidationContext,
                       validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {

    private val fragmentSpreads = LinkedHashMap<String, List<FragmentSpread>>()

    init {
        prepareFragmentMap()
    }

    private fun prepareFragmentMap() {
        val definitions = validationContext.document.definitions
        for (definition in definitions) {
            if (definition is FragmentDefinition) {
                val fragmentDefinition = definition
                fragmentSpreads.put(fragmentDefinition.name, gatherSpreads(fragmentDefinition))
            }
        }
    }

    private fun gatherSpreads(fragmentDefinition: FragmentDefinition): List<FragmentSpread> {
        val fragmentSpreads = ArrayList<FragmentSpread>()
        val visitor = object : QueryLanguageVisitor {
            override fun enter(node: Node, path: List<Node>) {
                if (node is FragmentSpread) {
                    fragmentSpreads.add(node)
                }
            }

            override fun leave(node: Node, path: List<Node>) {

            }
        }

        LanguageTraversal().traverse(fragmentDefinition, visitor)
        return fragmentSpreads
    }


    override fun checkFragmentDefinition(fragmentDefinition: FragmentDefinition) {
        val spreadPath = ArrayList<FragmentSpread>()
        detectCycleRecursive(fragmentDefinition.name, fragmentDefinition.name, spreadPath)
    }

    private fun detectCycleRecursive(fragmentName: String,
                                     initialName: String,
                                     spreadPath: MutableList<FragmentSpread>) {
        val fragmentSpreads = this.fragmentSpreads[fragmentName]

        if (fragmentSpreads != null) {
            outer@ for (fragmentSpread in fragmentSpreads) {

                if (fragmentSpread.name == initialName) {
                    val message = "Fragment cycles not allowed"
                    addError(ErrorFactory().newError(ValidationErrorType.FragmentCycle, spreadPath, message))
                    continue
                }
                for (spread in spreadPath) {
                    if (spread == fragmentSpread) {
                        continue@outer
                    }
                }
                spreadPath.add(fragmentSpread)
                detectCycleRecursive(fragmentSpread.name, initialName, spreadPath)
                spreadPath.removeAt(spreadPath.size - 1)
            }
        } else {
            val message = "Fragment $fragmentName not found"
            addError(ErrorFactory().newError(ValidationErrorType.FragmentCycle, spreadPath, message))

        }
    }
}
