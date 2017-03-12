package graphql.validation.rules


import graphql.language.Value
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType

class VariablesTypesMatcher {

    fun doesVariableTypesMatch(variableType: GraphQLType?,
                               variableDefaultValue: Value?,
                               expectedType: GraphQLType): Boolean {
        return checkType(effectiveType(variableType, variableDefaultValue), expectedType)
    }

    private fun effectiveType(variableType: GraphQLType?, defaultValue: Value?): GraphQLType? {
        if (defaultValue == null) return variableType
        if (variableType is GraphQLNonNull) return variableType
        return GraphQLNonNull(variableType!!)
    }

    private fun checkType(actualType: GraphQLType?, expectedType: GraphQLType): Boolean {

        if (expectedType is GraphQLNonNull) {
            if (actualType is GraphQLNonNull) {
                return checkType(actualType.wrappedType, expectedType.wrappedType)
            }
            return false
        }

        if (actualType is GraphQLNonNull) {
            return checkType(actualType.wrappedType, expectedType)
        }


        if (actualType is GraphQLList && expectedType is GraphQLList) {
            return checkType(actualType.wrappedType, expectedType.wrappedType)
        }
        return actualType === expectedType
    }

}
