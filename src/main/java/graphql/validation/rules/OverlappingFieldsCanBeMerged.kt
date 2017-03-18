package graphql.validation.rules


import graphql.execution.TypeFromAST
import graphql.language.*
import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLType
import graphql.validation.AbstractRule
import graphql.validation.ErrorFactory
import graphql.validation.ValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType.FieldsConflict
import java.util.*

class OverlappingFieldsCanBeMerged(validationContext: ValidationContext, validationErrorCollector: ValidationErrorCollector) : AbstractRule(validationContext, validationErrorCollector) {

    internal var errorFactory = ErrorFactory()


    private val alreadyChecked = ArrayList<FieldPair>()

    override fun leaveSelectionSet(selectionSet: SelectionSet) {
        val fieldMap = LinkedHashMap<String, MutableList<FieldAndType>>()
        val visitedFragmentSpreads = LinkedHashSet<String>()
        collectFields(fieldMap, selectionSet, validationContext.outputType, visitedFragmentSpreads)
        val conflicts = findConflicts(fieldMap)
        for (conflict in conflicts) {
            addError(errorFactory.newError(FieldsConflict, conflict.fields, conflict.reason))
        }
    }

    private fun findConflicts(fieldMap: Map<String, List<FieldAndType>>): List<Conflict> {
        val result = ArrayList<Conflict>()
        for (name in fieldMap.keys) {
            fieldMap[name]?.apply {
                for (i in indices) {
                    for (j in i + 1..size - 1) {
                        val conflict = findConflict(name, get(i), get(j))
                        if (conflict != null) {
                            result.add(conflict)
                        }
                    }
                }
            }
        }
        return result
    }

    private fun isAlreadyChecked(field1: Field, field2: Field): Boolean {
        for (fieldPair in alreadyChecked) {
            if (fieldPair.field1 == field1 && fieldPair.field2 == field2) {
                return true
            }
            if (fieldPair.field1 == field2 && fieldPair.field2 == field1) {
                return true
            }
        }
        return false
    }

    private fun findConflict(responseName: String, fieldAndType1: FieldAndType, fieldAndType2: FieldAndType): Conflict? {

        val field1 = fieldAndType1.field
        val field2 = fieldAndType2.field


        val type1 = fieldAndType1.graphQLType
        val type2 = fieldAndType2.graphQLType

        val fieldName1 = field1.name
        val fieldName2 = field2.name


        if (isAlreadyChecked(field1, field2)) {
            return null
        }
        alreadyChecked.add(FieldPair(field1, field2))

        // If the statically known parent types could not possibly apply at the same
        // time, then it is safe to permit them to diverge as they will not present
        // any ambiguity by differing.
        // It is known that two parent types could never overlap if they are
        // different Object types. Interface or Union types might overlap - if not
        // in the current state of the schema, then perhaps in some future version,
        // thus may not safely diverge.
        if (!sameType(fieldAndType1.parentType, fieldAndType1.parentType) &&
                fieldAndType1.parentType is GraphQLObjectType &&
                fieldAndType2.parentType is GraphQLObjectType) {
            return null
        }

        if (fieldName1 != fieldName2) {
            val reason = String.format("%s: %s and %s are different fields", responseName, fieldName1, fieldName2)
            return Conflict(responseName, reason, field1, field2)
        }

        if (!sameType(type1, type2)) {
            val name1 = type1?.name
            val name2 = type2?.name
            val reason = String.format("%s: they return differing types %s and %s", responseName, name1, name2)
            return Conflict(responseName, reason, field1, field2)
        }


        if (!sameArguments(field1.arguments, field2.arguments)) {
            val reason = String.format("%s: they have differing arguments", responseName)
            return Conflict(responseName, reason, field1, field2)
        }
        if (!sameDirectives(field1.directives, field2.directives)) {
            val reason = String.format("%s: they have differing directives", responseName)
            return Conflict(responseName, reason, field1, field2)
        }
        val selectionSet1 = field1.selectionSet
        val selectionSet2 = field2.selectionSet
        if (!selectionSet1.isEmpty() && !selectionSet2.isEmpty()) {
            val visitedFragmentSpreads = LinkedHashSet<String>()
            val subFieldMap = LinkedHashMap<String, MutableList<FieldAndType>>()
            collectFields(subFieldMap, selectionSet1, type1, visitedFragmentSpreads)
            collectFields(subFieldMap, selectionSet2, type2, visitedFragmentSpreads)
            val subConflicts = findConflicts(subFieldMap)
            if (subConflicts.isNotEmpty()) {
                val reason = String.format("%s: %s", responseName, joinReasons(subConflicts))
                val fields = ArrayList<Field>()
                fields.add(field1)
                fields.add(field2)
                fields.addAll(collectFields(subConflicts))
                return Conflict(responseName, reason, fields)
            }
        }

        return null

    }

    private fun collectFields(conflicts: List<Conflict>): List<Field> {
        val result = ArrayList<Field>()
        for (conflict in conflicts) {
            result.addAll(conflict.fields)
        }
        return result
    }

    private fun joinReasons(conflicts: List<Conflict>): String {
        val result = StringBuilder()
        result.append("(")
        for (conflict in conflicts) {
            result.append(conflict.reason)
            result.append(", ")
        }
        result.delete(result.length - 2, result.length)
        result.append(")")
        return result.toString()
    }

    private fun sameType(type1: GraphQLType?, type2: GraphQLType?): Boolean {
        if (type1 == null || type2 == null) return true
        return type1 == type2
    }

    private fun sameValue(value1: Value?, value2: Value?): Boolean {
        if (value1 == null && value2 == null) return true
        if (value1 == null) return false
        if (value2 == null) return false
        return AstComparator().isEqual(value1, value2)
    }

    private fun sameArguments(arguments1: List<Argument>, arguments2: List<Argument>): Boolean {
        if (arguments1.size != arguments2.size) return false
        for ((name, value) in arguments1) {
            val matchedArgument = findArgumentByName(name, arguments2) ?: return false
            if (!sameValue(value, matchedArgument.value)) return false
        }
        return true
    }

    private fun findArgumentByName(name: String, arguments: List<Argument>): Argument? {
        return arguments.firstOrNull { it.name == name }
    }

    private fun sameDirectives(directives1: List<Directive>, directives2: List<Directive>): Boolean {
        if (directives1.size != directives2.size) return false
        for (directive in directives1) {
            val matchedDirective = findDirectiveByName(directive.name, directives2) ?: return false
            if (!sameArguments(directive.arguments, matchedDirective.arguments)) return false
        }
        return true
    }

    private fun findDirectiveByName(name: String, directives: List<Directive>): Directive? {
        return directives.firstOrNull { it.name == name }
    }


    private fun collectFields(fieldMap: MutableMap<String, MutableList<FieldAndType>>,
                              selectionSet: SelectionSet?,
                              parentType: GraphQLType?,
                              visitedFragmentSpreads: MutableSet<String>) {

        if (selectionSet != null) {
            for (selection in selectionSet.selections) {
                if (selection is Field) {
                    collectFieldsForField(fieldMap, parentType, selection)
                } else if (selection is InlineFragment) {
                    collectFieldsForInlineFragment(fieldMap, visitedFragmentSpreads, parentType, selection)

                } else if (selection is FragmentSpread) {
                    collectFieldsForFragmentSpread(fieldMap, visitedFragmentSpreads, selection)
                }
            }
        }
    }

    private fun collectFieldsForFragmentSpread(fieldMap: MutableMap<String, MutableList<FieldAndType>>,
                                               visitedFragmentSpreads: MutableSet<String>,
                                               selection: FragmentSpread) {
        val fragmentSpread = selection
        val fragment = validationContext.fragment(fragmentSpread.name) ?: return
        if (visitedFragmentSpreads.contains(fragment.name)) {
            return
        }
        visitedFragmentSpreads.add(fragment.name)
        val graphQLType = TypeFromAST.getTypeFromAST(validationContext.schema,
                                                     fragment.typeCondition) as GraphQLOutputType?
        collectFields(fieldMap, fragment.selectionSet, graphQLType, visitedFragmentSpreads)
    }

    private fun collectFieldsForInlineFragment(fieldMap: MutableMap<String, MutableList<FieldAndType>>,
                                               visitedFragmentSpreads: MutableSet<String>,
                                               parentType: GraphQLType?,
                                               selection: InlineFragment) {
        val inlineFragment = selection
        val graphQLType = if (inlineFragment.typeCondition != null)
            TypeFromAST.getTypeFromAST(validationContext.schema, inlineFragment.typeCondition) as GraphQLOutputType
        else
            parentType
        collectFields(fieldMap, inlineFragment.selectionSet, graphQLType, visitedFragmentSpreads)
    }

    private fun collectFieldsForField(fieldMap: MutableMap<String, MutableList<FieldAndType>>,
                                      parentType: GraphQLType?,
                                      selection: Field) {
        val field = selection
        val responseName = field.alias ?: field.name
        if (!fieldMap.containsKey(responseName)) {
            fieldMap.put(responseName, ArrayList<FieldAndType>())
        }
        var fieldType: GraphQLOutputType? = null
        if (parentType is GraphQLFieldsContainer) {
            val fieldsContainer = parentType
            val fieldDefinition = fieldsContainer.fieldDefinitions.find { it.name == field.name }
            fieldType = fieldDefinition?.type
        }
        fieldMap[responseName]?.add(FieldAndType(field, fieldType, parentType))
    }

    private class FieldPair(internal var field1: Field, internal var field2: Field)

    private class Conflict {
        internal var responseName: String
        internal var reason: String
        internal var fields: MutableList<Field> = mutableListOf()

        constructor(responseName: String, reason: String, field1: Field, field2: Field) {
            this.responseName = responseName
            this.reason = reason
            this.fields.add(field1)
            this.fields.add(field2)
        }

        constructor(responseName: String, reason: String, fields: List<Field>) {
            this.responseName = responseName
            this.reason = reason
            this.fields.addAll(fields)
        }

    }


    private class FieldAndType(internal var field: Field,
                               internal var graphQLType: GraphQLType?,
                               internal var parentType: GraphQLType?)
}
