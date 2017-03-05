package graphql.validation.rules


import graphql.ShouldNotHappenException
import graphql.execution.TypeFromAST
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.schema.*
import graphql.validation.*
import java.util.*

class PossibleFragmentSpreads(validationContext: ValidationContext,
                              validationErrorCollector: ValidationErrorCollector)
    : AbstractRule(validationContext, validationErrorCollector) {


    override fun checkInlineFragment(inlineFragment: InlineFragment) {
        val fragType = validationContext.outputType
        val parentType = validationContext.parentType
        if (fragType == null || parentType == null) return
        if (!doTypesOverlap(fragType, parentType)) {
            val message = String.format("Fragment cannot be spread here as objects of " + "type %s can never be of type %s", parentType, fragType)
            addError(ValidationError(ValidationErrorType.InvalidFragmentType, inlineFragment.sourceLocation, message))

        }
    }

    override fun checkFragmentSpread(fragmentSpread: FragmentSpread) {
        val fragment = validationContext.getFragment(fragmentSpread.name) ?: return
        val typeCondition = TypeFromAST.getTypeFromAST(validationContext.schema, fragment.typeCondition)
        val parentType = validationContext.parentType
        if (typeCondition == null || parentType == null) return

        if (!doTypesOverlap(typeCondition, parentType)) {
            val message = String.format("Fragment %s cannot be spread here as objects of " + "type %s can never be of type %s", fragmentSpread.name, parentType, typeCondition)
            addError(ValidationError(ValidationErrorType.InvalidFragmentType, fragmentSpread.sourceLocation, message))
        }
    }

    private fun doTypesOverlap(type: GraphQLType, parent: GraphQLCompositeType): Boolean {
        if (type === parent) {
            return true
        }

        val possibleParentTypes: List<GraphQLType>
        if (parent is GraphQLObjectType) {
            possibleParentTypes = listOf<GraphQLType>(parent)
        } else if (parent is GraphQLInterfaceType) {
            possibleParentTypes = SchemaUtil().findImplementations(validationContext.schema, parent)
        } else if (parent is GraphQLUnionType) {
            possibleParentTypes = parent.types()
        } else {
            throw ShouldNotHappenException()
        }
        val possibleConditionTypes: List<GraphQLType>
        if (type is GraphQLObjectType) {
            possibleConditionTypes = listOf<GraphQLType>(type)
        } else if (type is GraphQLInterfaceType) {
            possibleConditionTypes = SchemaUtil().findImplementations(validationContext.schema, type)
        } else if (type is GraphQLUnionType) {
            possibleConditionTypes = type.types()
        } else {
            throw ShouldNotHappenException()
        }

        return !Collections.disjoint(possibleParentTypes, possibleConditionTypes)

    }
}
