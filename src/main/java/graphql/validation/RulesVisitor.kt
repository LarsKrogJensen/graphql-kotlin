package graphql.validation


import graphql.language.*

import java.util.*

class RulesVisitor(private val validationContext: ValidationContext,
                   rules: List<AbstractRule>,
                   subVisitor: Boolean = false) : QueryLanguageVisitor {

    private val _rules = ArrayList<AbstractRule>()
    private var _subVisitor: Boolean = false
    private val _rulesVisitingFragmentSpreads = ArrayList<AbstractRule>()
    private val _rulesToSkipByUntilNode = IdentityHashMap<Node, List<AbstractRule>>()
    private val _rulesToSkip = LinkedHashSet<AbstractRule>()

    init {
        this._subVisitor = subVisitor
        this._rules.addAll(rules)
        this._subVisitor = subVisitor
        findRulesVisitingFragmentSpreads()
    }

    private fun findRulesVisitingFragmentSpreads() {
        for (rule in _rules) {
            if (rule.isVisitFragmentSpreads) {
                _rulesVisitingFragmentSpreads.add(rule)
            }
        }
    }

    override fun enter(node: Node, path: List<Node>) {
        validationContext.traversalContext.enter(node, path)
        val tmpRulesSet = LinkedHashSet(this._rules)
        tmpRulesSet.removeAll(_rulesToSkip)
        val rulesToConsider = ArrayList(tmpRulesSet)
        when (node) {
            is Argument            -> checkArgument(node, rulesToConsider)
            is TypeName            -> checkTypeName(node, rulesToConsider)
            is VariableDefinition  -> checkVariableDefinition(node, rulesToConsider)
            is Field               -> checkField(node, rulesToConsider)
            is InlineFragment      -> checkInlineFragment(node, rulesToConsider)
            is Directive           -> checkDirective(node, path, rulesToConsider)
            is FragmentSpread      -> checkFragmentSpread(node, rulesToConsider, path)
            is FragmentDefinition  -> checkFragmentDefinition(node, rulesToConsider)
            is OperationDefinition -> checkOperationDefinition(node, rulesToConsider)
            is VariableReference   -> checkVariable(node, rulesToConsider)
            is SelectionSet        -> checkSelectionSet(node, rulesToConsider)
        }
    }

    private fun checkArgument(node: Argument, rules: List<AbstractRule>) {
        for (rule in rules) {
            rule.checkArgument(node)
        }
    }

    private fun checkTypeName(node: TypeName, rules: List<AbstractRule>) {
        rules.forEach { rule -> rule.checkTypeName(node) }
    }


    private fun checkVariableDefinition(variableDefinition: VariableDefinition, rules: List<AbstractRule>) {
        for (rule in rules) {
            rule.checkVariableDefinition(variableDefinition)
        }
    }

    private fun checkField(field: Field, rules: List<AbstractRule>) {
        for (rule in rules) {
            rule.checkField(field)
        }
    }

    private fun checkInlineFragment(inlineFragment: InlineFragment, rules: List<AbstractRule>) {
        for (rule in rules) {
            rule.checkInlineFragment(inlineFragment)
        }
    }

    private fun checkDirective(directive: Directive, ancestors: List<Node>, rules: List<AbstractRule>) {
        for (rule in rules) {
            rule.checkDirective(directive, ancestors)
        }
    }

    private fun checkFragmentSpread(fragmentSpread: FragmentSpread,
                                    rules: List<AbstractRule>,
                                    path: List<Node>) {
        for (rule in rules) {
            rule.checkFragmentSpread(fragmentSpread)
        }
        val rulesVisitingFragmentSpreads = getRulesVisitingFragmentSpreads(rules)
        if (rulesVisitingFragmentSpreads.isNotEmpty()) {
            val fragment = validationContext.getFragment(fragmentSpread.name)
            if (!path.contains(fragment as Node)) {
                LanguageTraversal(path).traverse(fragment, RulesVisitor(validationContext, rulesVisitingFragmentSpreads, true))
            }
        }
    }

    private fun getRulesVisitingFragmentSpreads(rules: List<AbstractRule>): List<AbstractRule> {
        return rules.filter { it.isVisitFragmentSpreads }
    }

    private fun checkFragmentDefinition(fragmentDefinition: FragmentDefinition, rules: List<AbstractRule>) {
        if (!_subVisitor) {
            _rulesToSkipByUntilNode.put(fragmentDefinition, ArrayList(_rulesVisitingFragmentSpreads))
            _rulesToSkip.addAll(_rulesVisitingFragmentSpreads)
        }

        rules.filterNot { !_subVisitor && it.isVisitFragmentSpreads }
                .forEach { it.checkFragmentDefinition(fragmentDefinition) }
    }

    private fun checkOperationDefinition(operationDefinition: OperationDefinition, rules: List<AbstractRule>) {
        rules.forEach { rule ->
            rule.checkOperationDefinition(operationDefinition)
        }
    }

    private fun checkSelectionSet(selectionSet: SelectionSet, rules: List<AbstractRule>) {
        rules.forEach { rule ->
            rule.checkSelectionSet(selectionSet)
        }
    }

    private fun checkVariable(variableReference: VariableReference, rules: List<AbstractRule>) {
        rules.forEach { rule ->
            rule.checkVariable(variableReference)
        }
    }


    override fun leave(node: Node, path: List<Node>) {
        validationContext.traversalContext.leave(node, path)

        when (node) {
            is Document            -> documentFinished(node)
            is OperationDefinition -> leaveOperationDefinition(node)
            is SelectionSet        -> leaveSelectionSet(node)
        }

        if (_rulesToSkipByUntilNode.containsKey(node)) {
            _rulesToSkip.removeAll(_rulesToSkipByUntilNode[node] as Iterable<AbstractRule>)
            _rulesToSkipByUntilNode.remove(node)
        }
    }

    private fun leaveSelectionSet(selectionSet: SelectionSet) {
        for (rule in _rules) {
            rule.leaveSelectionSet(selectionSet)
        }
    }

    private fun leaveOperationDefinition(operationDefinition: OperationDefinition) {
        for (rule in _rules) {
            rule.leaveOperationDefinition(operationDefinition)
        }
    }

    private fun documentFinished(document: Document) {
        for (rule in _rules) {
            rule.documentFinished(document)
        }
    }
}
