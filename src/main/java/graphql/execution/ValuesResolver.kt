package graphql.execution


import graphql.GraphQLException
import graphql.language.*
import graphql.schema.*

import java.util.*

class ValuesResolver {


    fun getVariableValues(schema: GraphQLSchema,
                          variableDefinitions: List<VariableDefinition>,
                          inputs: Map<String, Any>): Map<String, Any?> {
        val result = LinkedHashMap<String, Any?>()
        for (variableDefinition in variableDefinitions) {
            result.put(variableDefinition.name, variableValue(schema, variableDefinition, inputs[variableDefinition.name]))
        }
        return result
    }


    fun argumentValues(argumentTypes: List<GraphQLArgument>,
                       arguments: List<Argument>,
                       variables: Map<String, Any?>): Map<String, Any?> {

        val result = LinkedHashMap<String, Any?>()
        val argumentMap = argumentMap(arguments)
        for (fieldArgument in argumentTypes) {
            val argument = argumentMap[fieldArgument.name]
            val value = if (argument != null) {
                coerceValueAst(fieldArgument.type, argument.value, variables)
            } else {
                fieldArgument.defaultValue
            }
            result.put(fieldArgument.name, value)
        }
        return result
    }


    private fun argumentMap(arguments: List<Argument>): Map<String, Argument> {
        val result = LinkedHashMap<String, Argument>()
        for (argument in arguments) {
            result.put(argument.name, argument)
        }
        return result
    }


    private fun variableValue(schema: GraphQLSchema,
                              variableDefinition: VariableDefinition,
                              inputValue: Any?): Any? {
        val type = TypeFromAST.getTypeFromAST(schema, variableDefinition.type!!)

        if (!isValid(type, inputValue)) {
            throw GraphQLException("Invalid value for type")
        }

        if (inputValue == null && variableDefinition.defaultValue != null) {
            return coerceValueAst(type, variableDefinition.defaultValue, emptyMap())
        }

        return coerceValue(type, inputValue)
    }

    private fun isValid(type: GraphQLType, inputValue: Any?): Boolean {
        return true
    }

    private fun coerceValue(graphQLType: GraphQLType, value: Any?): Any? {
        if (graphQLType is GraphQLNonNull) {
            return coerceValue(graphQLType.wrappedType, value) ?: throw GraphQLException("Null value for NonNull type " + graphQLType)
        }

        if (value == null) return null

        if (graphQLType is GraphQLScalarType) {
            return coerceValueForScalar(graphQLType, value)
        } else if (graphQLType is GraphQLEnumType) {
            return coerceValueForEnum(graphQLType, value)
        } else if (graphQLType is GraphQLList) {
            return coerceValueForList(graphQLType, value)
        } else if (graphQLType is GraphQLInputObjectType) {
            return coerceValueForInputObjectType(graphQLType, value as Map<String, Any>)
        } else {
            throw GraphQLException("unknown type " + graphQLType)
        }
    }

    private fun coerceValueForInputObjectType(inputObjectType: GraphQLInputObjectType, input: Map<String, Any>): Any {
        return inputObjectType.fields
                .filter { input.containsKey(it.name) || alwaysHasValue(it) }
                .associateBy({ it.type }, { coerceValue(it.type, input[it.name]) ?: it.defaultValue })
    }

    private fun alwaysHasValue(inputField: GraphQLInputObjectField): Boolean {
        return inputField.defaultValue != null || inputField.type is GraphQLNonNull
    }

    private fun coerceValueForScalar(graphQLScalarType: GraphQLScalarType, value: Any): Any? {
        return graphQLScalarType.coercing.parseValue(value)
    }

    private fun coerceValueForEnum(graphQLEnumType: GraphQLEnumType, value: Any): Any? {
        return graphQLEnumType.coercing.parseValue(value)
    }

    private fun coerceValueForList(graphQLList: GraphQLList, value: Any): List<*> {
        if (value is Iterable<*>) {
            return value.map { coerceValue(graphQLList.wrappedType, it) }
        } else {
            return listOf(coerceValue(graphQLList.wrappedType, value))
        }
    }

    private fun coerceValueAst(type: GraphQLType,
                               inputValue: Value?,
                               variables: Map<String, Any?>): Any? {
        if (inputValue is VariableReference) {
            return variables[inputValue.name]
        }
        if (type is GraphQLScalarType) {
            return type.coercing.parseLiteral(inputValue)
        }
        if (type is GraphQLNonNull) {
            return coerceValueAst(type.wrappedType, inputValue, variables)
        }
        if (type is GraphQLInputObjectType) {
            return coerceValueAstForInputObject(type, inputValue as ObjectValue, variables)
        }
        if (type is GraphQLEnumType) {
            return type.coercing.parseLiteral(inputValue)
        }
        if (type is GraphQLList) {
            return coerceValueAstForList(type, inputValue, variables)
        }
        return null
    }

    private fun coerceValueAstForList(graphQLList: GraphQLList, value: Value?, variables: Map<String, Any?>): Any {
        if (value is ArrayValue) {
            return value.values.map { coerceValueAst(graphQLList.wrappedType, it, variables) }
        } else {
            return listOf(coerceValueAst(graphQLList.wrappedType, value, variables))
        }
    }

    private fun coerceValueAstForInputObject(type: GraphQLInputObjectType,
                                             inputValue: ObjectValue,
                                             variables: Map<String, Any?>): Any {
        val result = LinkedHashMap<String, Any?>()

        val inputValueFieldsByName = mapObjectValueFieldsByName(inputValue)

        for (inputTypeField in type.fields) {
            if (inputValueFieldsByName.containsKey(inputTypeField.name)) {
                inputValueFieldsByName[inputTypeField.name]?.let { (name, value) ->
                    val fieldValue = coerceValueAst(inputTypeField.type, value, variables) ?: inputTypeField.defaultValue
                    result.put(name, fieldValue)
                }
            } else if (inputTypeField.defaultValue != null) {
                result.put(inputTypeField.name, inputTypeField.defaultValue)
            } else if (inputTypeField.type is GraphQLNonNull) {
                // Possibly overkill; an object literal with a missing non null field shouldn't pass validation
                throw GraphQLException("Null value for NonNull type " + inputTypeField.type)
            }
        }
        return result
    }

    private fun mapObjectValueFieldsByName(inputValue: ObjectValue): Map<String, ObjectField> {
        val inputValueFieldsByName = LinkedHashMap<String, ObjectField>()
        for (objectField in inputValue.objectFields) {
            inputValueFieldsByName.put(objectField.name, objectField)
        }
        return inputValueFieldsByName
    }

}
