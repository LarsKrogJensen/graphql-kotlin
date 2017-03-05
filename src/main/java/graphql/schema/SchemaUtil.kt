package graphql.schema


import graphql.GraphQLException
import graphql.introspection.Introspection
import java.util.*

class SchemaUtil {

    fun isLeafType(type: GraphQLType): Boolean {
        val unmodifiedType = getUnmodifiedType(type)
        return unmodifiedType is GraphQLScalarType || unmodifiedType is GraphQLEnumType
    }

    fun isInputType(graphQLType: GraphQLType): Boolean {
        val unmodifiedType = getUnmodifiedType(graphQLType)
        return unmodifiedType is GraphQLScalarType
                || unmodifiedType is GraphQLEnumType
                || unmodifiedType is GraphQLInputObjectType
    }

    fun getUnmodifiedType(graphQLType: GraphQLType): GraphQLUnmodifiedType {
        if (graphQLType is GraphQLModifiedType) {
            return getUnmodifiedType(graphQLType.wrappedType)
        }
        return graphQLType as GraphQLUnmodifiedType
    }


    private fun collectTypes(root: GraphQLType, result: MutableMap<String, GraphQLType>) {
        if (root is GraphQLNonNull) {
            collectTypes(root.wrappedType, result)
        } else if (root is GraphQLList) {
            collectTypes(root.wrappedType, result)
        } else if (root is GraphQLEnumType) {
            result.put(root.name, root)
        } else if (root is GraphQLScalarType) {
            result.put(root.name, root)
        } else if (root is GraphQLObjectType) {
            collectTypesForObjects(root, result)
        } else if (root is GraphQLInterfaceType) {
            collectTypesForInterfaces(root, result)
        } else if (root is GraphQLUnionType) {
            collectTypesForUnions(root, result)
        } else if (root is GraphQLInputObjectType) {
            collectTypesForInputObjects(root, result)
        } else if (root is GraphQLTypeReference) {
            // nothing to do
        } else {
            throw RuntimeException("Unknown type " + root)
        }
    }

    private fun collectTypesForUnions(unionType: GraphQLUnionType, result: MutableMap<String, GraphQLType>) {
        result.put(unionType.name, unionType)
        for (type in unionType.types()) {
            collectTypes(type, result)
        }

    }

    private fun collectTypesForInterfaces(interfaceType: GraphQLInterfaceType, result: MutableMap<String, GraphQLType>) {
        if (result.containsKey(interfaceType.name) && result[interfaceType.name] !is TypeReference) return
        result.put(interfaceType.name, interfaceType)

        for (fieldDefinition in interfaceType.fieldDefinitions) {
            collectTypes(fieldDefinition.type, result)
            for (fieldArgument in fieldDefinition.arguments) {
                collectTypes(fieldArgument.type, result)
            }
        }
    }


    private fun collectTypesForObjects(objectType: GraphQLObjectType, result: MutableMap<String, GraphQLType>) {
        if (result.containsKey(objectType.name) && result[objectType.name] !is TypeReference) return
        result.put(objectType.name, objectType)

        for (fieldDefinition in objectType.fieldDefinitions) {
            collectTypes(fieldDefinition.type, result)
            for (fieldArgument in fieldDefinition.arguments) {
                collectTypes(fieldArgument.type, result)
            }
        }
        for (interfaceType in objectType.interfaces) {
            collectTypes(interfaceType, result)
        }
    }

    private fun collectTypesForInputObjects(objectType: GraphQLInputObjectType, result: MutableMap<String, GraphQLType>) {
        if (result.containsKey(objectType.name) && result[objectType.name] !is TypeReference) return
        result.put(objectType.name, objectType)

        for (fieldDefinition in objectType.fields) {
            collectTypes(fieldDefinition.type, result)
        }
    }


    fun allTypes(schema: GraphQLSchema, dictionary: Set<GraphQLType>?): Map<String, GraphQLType> {
        val typesByName = LinkedHashMap<String, GraphQLType>()
        collectTypes(schema.queryType, typesByName)
        if (schema.isSupportingMutations) {
            collectTypes(schema.mutationType!!, typesByName)
        }
        if (dictionary != null) {
            for (type in dictionary) {
                collectTypes(type, typesByName)
            }
        }
        collectTypes(Introspection.__Schema, typesByName)
        return typesByName
    }

    fun findImplementations(schema: GraphQLSchema, interfaceType: GraphQLInterfaceType): List<GraphQLObjectType> {
        return allTypes(schema, schema.dictionary).values
                .filterIsInstance<GraphQLObjectType>()
                .map { it }
                .filter { it.interfaces.contains(interfaceType) }
    }


    internal fun replaceTypeReferences(schema: GraphQLSchema) {
        val typeMap = allTypes(schema, schema.dictionary)
        for (type in typeMap.values) {
            if (type is GraphQLFieldsContainer) {
                resolveTypeReferencesForFieldsContainer(type, typeMap)
            }
            if (type is GraphQLInputFieldsContainer) {
                resolveTypeReferencesForInputFieldsContainer(type, typeMap)
            }
        }
    }

    private fun resolveTypeReferencesForFieldsContainer(fieldsContainer: GraphQLFieldsContainer, typeMap: Map<String, GraphQLType>) {
        for (fieldDefinition in fieldsContainer.fieldDefinitions) {
            fieldDefinition.replaceTypeReferences(typeMap)
            for (argument in fieldDefinition.arguments) {
                argument.replaceTypeReferences(typeMap)
            }
        }
    }

    private fun resolveTypeReferencesForInputFieldsContainer(fieldsContainer: GraphQLInputFieldsContainer,
                                                             typeMap: Map<String, GraphQLType>) {
        for (fieldDefinition in fieldsContainer.fieldDefinitions) {
            fieldDefinition.replaceTypeReferences(typeMap)
        }
    }

    internal fun resolveTypeReference(type: GraphQLType, typeMap: Map<String, GraphQLType>): GraphQLType {
        if (type is GraphQLTypeReference || typeMap.containsKey(type.name)) {
            val resolvedType = typeMap[type.name] ?: throw GraphQLException("type " + type.name + " not found in schema")
            return resolvedType
        }
        if (type is GraphQLList) {
            type.replaceTypeReferences(typeMap)
        }
        if (type is GraphQLNonNull) {
            type.replaceTypeReferences(typeMap)
        }
        return type
    }

    internal fun resolveTypeReferences(types: List<GraphQLType>, typeMap: Map<String, GraphQLType>): List<GraphQLType> {
        return types.map { resolveTypeReference(it, typeMap) }
    }
}