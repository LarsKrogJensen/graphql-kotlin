package graphql.validation


import graphql.ShouldNotHappenException
import graphql.language.*
import graphql.schema.*

import java.util.LinkedHashMap

class ValidationUtil {

    fun getUnmodifiedType(type: Type?): TypeName {
        when (type) {
            is ListType    -> return getUnmodifiedType(type.type)
            is NonNullType -> return getUnmodifiedType(type.type)
            is TypeName    -> return type
            else           -> throw ShouldNotHappenException()
        }
    }

    fun isValidLiteralValue(value: Value?, type: GraphQLType): Boolean {
        if (value == null) {
            return type !is GraphQLNonNull
        }
        if (value is VariableReference) {
            return true
        }
        if (type is GraphQLNonNull) {
            return isValidLiteralValue(value, type.wrappedType)
        }

        if (type is GraphQLScalarType) {
            return type.coercing.parseLiteral(value) != null
        }
        if (type is GraphQLEnumType) {
            return type.coercing.parseLiteral(value) != null
        }

        if (type is GraphQLList) {
            return isValidLiteralValue(value, type)
        }
        if (type is GraphQLInputObjectType) {
            return isValidLiteralValue(value, type)
        }

        return false
    }

    private fun isValidLiteralValue(value: Value, type: GraphQLInputObjectType): Boolean {
        if (value !is ObjectValue) return false
        val objectValue = value
        val objectFieldMap = fieldMap(objectValue)

        if (isFieldMissing(type, objectFieldMap)) return false

        for ((name, value1) in objectValue.objectFields()) {
            val inputObjectField = type.field(name) ?: return false
            if (!isValidLiteralValue(value1, inputObjectField.type)) return false

        }
        return true
    }

    private fun isFieldMissing(type: GraphQLInputObjectType, objectFieldMap: Map<String, ObjectField>): Boolean {
        for (inputObjectField in type.fields) {
            if (!objectFieldMap.containsKey(inputObjectField.name) && inputObjectField.type is GraphQLNonNull)
                return true
        }
        return false
    }

    private fun fieldMap(objectValue: ObjectValue): Map<String, ObjectField> {
        val result = LinkedHashMap<String, ObjectField>()
        for (objectField in objectValue.objectFields()) {
            result.put(objectField.name, objectField)
        }
        return result
    }

    private fun isValidLiteralValue(value: Value, type: GraphQLList): Boolean {
        val wrappedType = type.wrappedType
        if (value is ArrayValue) {
            for (innerValue in value.values) {
                if (!isValidLiteralValue(innerValue, wrappedType)) return false
            }
            return true
        } else {
            return isValidLiteralValue(value, wrappedType)
        }
    }

}
