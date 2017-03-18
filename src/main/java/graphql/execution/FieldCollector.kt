package graphql.execution


import graphql.execution.TypeFromAST.getTypeFromAST
import graphql.language.*
import graphql.schema.*
import java.util.*

class FieldCollector {

    private val _conditionalNodes: ConditionalNodes = ConditionalNodes()

    private val _schemaUtil = SchemaUtil()

    fun collectFields(executionContext: ExecutionContext,
                      type: GraphQLObjectType,
                      selectionSet: SelectionSet,
                      visitedFragments: MutableList<String>,
                      fields: MutableMap<String, MutableList<Field>>) {

        for (selection in selectionSet.selections) {
            when (selection) {
                is Field          -> collectField(executionContext, fields, selection)
                is InlineFragment -> collectInlineFragment(executionContext, type, visitedFragments, fields, selection)
                is FragmentSpread -> collectFragmentSpread(executionContext, type, visitedFragments, fields, selection)
            }
        }
    }

    private fun collectFragmentSpread(executionContext: ExecutionContext,
                                      type: GraphQLObjectType,
                                      visitedFragments: MutableList<String>,
                                      fields: MutableMap<String, MutableList<Field>>,
                                      fragmentSpread: FragmentSpread) {
        if (visitedFragments.contains(fragmentSpread.name)) {
            return
        }
        if (!_conditionalNodes.shouldInclude(executionContext, fragmentSpread.directives)) {
            return
        }
        visitedFragments.add(fragmentSpread.name)
        executionContext.fragment(fragmentSpread.name)?.let {
            if (!_conditionalNodes.shouldInclude(executionContext, it.directives)) {
                return
            }
            if (!doesFragmentConditionMatch(executionContext, it, type)) {
                return
            }
            collectFields(executionContext, type, it.selectionSet, visitedFragments, fields)
        }

    }

    private fun collectInlineFragment(executionContext: ExecutionContext,
                                      type: GraphQLObjectType,
                                      visitedFragments: MutableList<String>,
                                      fields: MutableMap<String, MutableList<Field>>,
                                      inlineFragment: InlineFragment) {
        if (!_conditionalNodes.shouldInclude(executionContext, inlineFragment.directives) ||
                !doesFragmentConditionMatch(executionContext, inlineFragment, type)) {
            return
        }
        collectFields(executionContext, type, inlineFragment.selectionSet, visitedFragments, fields)
    }

    private fun collectField(executionContext: ExecutionContext,
                             fields: MutableMap<String, MutableList<Field>>,
                             field: Field) {
        if (!_conditionalNodes.shouldInclude(executionContext, field.directives)) {
            return
        }
        val name = getFieldEntryKey(field)
        if (!fields.containsKey(name)) {
            fields.put(name, ArrayList<Field>())
        }
        fields[name]?.add(field)
    }

    private fun getFieldEntryKey(field: Field): String {
        if (field.alias != null)
            return field.alias!!
        else
            return field.name
    }


    private fun doesFragmentConditionMatch(executionContext: ExecutionContext,
                                           inlineFragment: InlineFragment,
                                           type: GraphQLObjectType): Boolean {
        val conditionType = getTypeFromAST(executionContext.graphQLSchema,
                                           inlineFragment.typeCondition)
        return checkTypeCondition(executionContext, type, conditionType)
    }

    private fun doesFragmentConditionMatch(executionContext: ExecutionContext,
                                           fragmentDefinition: FragmentDefinition,
                                           type: GraphQLObjectType): Boolean {
        val conditionType = getTypeFromAST(executionContext.graphQLSchema, fragmentDefinition.typeCondition)
        return checkTypeCondition(executionContext, type, conditionType)
    }

    private fun checkTypeCondition(executionContext: ExecutionContext, type: GraphQLObjectType, conditionType: GraphQLType?): Boolean {
        if (conditionType == type) {
            return true
        }

        if (conditionType is GraphQLInterfaceType) {
            val implementations = _schemaUtil.findImplementations(executionContext.graphQLSchema, conditionType)
            return implementations.contains(type)
        } else if (conditionType is GraphQLUnionType) {
            return conditionType.types().contains(type)
        }
        return false
    }


}
