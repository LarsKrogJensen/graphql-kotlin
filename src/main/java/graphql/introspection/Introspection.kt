package graphql.introspection


import graphql.GraphQLBoolean
import graphql.GraphQLString
import graphql.GraphQLStringNonNull
import graphql.schema.*
import graphql.util.succeeded
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture


object Introspection {

    enum class TypeKind {
        SCALAR,
        OBJECT,
        INTERFACE,
        UNION,
        ENUM,
        INPUT_OBJECT,
        LIST,
        NON_NULL
    }

    var __TypeKind = newEnum {
        name = "__TypeKind"
        description = "An enum describing what kind of type a given __Type is"
        value {
            name = "SCALAR"
            description = "Indicates this type is a scalar."
            value = TypeKind.SCALAR
        }
        value {
            name = "OBJECT"
            description = "Indicates this type is an object. `fields` and `interfaces` are valid fields."
            value = TypeKind.OBJECT
        }
        value {
            name = "INTERFACE"
            description = "Indicates this type is an interface. `fields` and `possibleTypes` are valid fields."
            value = TypeKind.INTERFACE
        }
        value {
            name = "UNION"
            description = "Indicates this type is a union. `possibleTypes` is a valid field."
            value = TypeKind.UNION
        }
        value {
            name = "ENUM"
            description = "Indicates this type is an enum. `enumValues` is a valid field."
            value = TypeKind.ENUM
        }
        value {
            name = "INPUT_OBJECT"
            description = "Indicates this type is an input object. `inputFields` is a valid field."
            value = TypeKind.INPUT_OBJECT
        }
        value {
            name = "LIST"
            description = "Indicates this type is a list. `ofType` is a valid field."
            value = TypeKind.LIST
        }
    }

    private val kindDataFetcher: DataFetcher<TypeKind> = { environment ->
        succeeded(when (environment.source<Any>()) {
                      is GraphQLScalarType      -> TypeKind.SCALAR
                      is GraphQLObjectType      -> TypeKind.OBJECT
                      is GraphQLInterfaceType   -> TypeKind.INTERFACE
                      is GraphQLUnionType       -> TypeKind.UNION
                      is GraphQLEnumType        -> TypeKind.ENUM
                      is GraphQLInputObjectType -> TypeKind.INPUT_OBJECT
                      is GraphQLList            -> TypeKind.LIST
                      is GraphQLNonNull         -> TypeKind.NON_NULL
                      else                      -> throw RuntimeException("Unknown kind of type: " + environment.source<Any>())
                  })
    }

    val __InputValue = newObject {
        name = "__InputValue"
        field<String> {
            name = "name"
            type = GraphQLStringNonNull
        }
        field<String> {
            name = "description"
        }
        field<String> {
            name = "type"
            type = GraphQLTypeReference("__Type")
        }
        field<String> {
            name = "defaultValue"
            type = GraphQLTypeReference("defaultValue")
            fetcher = { environment ->
                val promise = CompletableFuture<String>()
                val source: Any = environment.source()
                promise.complete(
                        if (source is GraphQLArgument) {
                            source.defaultValue?.toString()
                        } else if (source is GraphQLInputObjectField) {
                            source.defaultValue?.toString()
                        } else null)
                promise
            }
        }
    }

    val __Field = newObject {
        name = "__Field"
        field<String> {
            name = "name"
            type = GraphQLStringNonNull
        }
        field<String> {
            name = "description"
            type = GraphQLString
        }
        field<List<GraphQLArgument>> {
            name = "args"
            type = GraphQLNonNull(GraphQLList(GraphQLNonNull(__InputValue)))
            fetcher = { environment ->
                val type = environment.source<GraphQLFieldDefinition<*>>()
                succeeded(type.arguments)
            }
        }
        field<Any> {
            name = "type"
            type = GraphQLNonNull(GraphQLTypeReference("__Type"))
        }
        field<Boolean> {
            name = "isDeprecated"
            type = GraphQLNonNull(GraphQLBoolean)
            fetcher = { environment ->
                val type = environment.source<GraphQLFieldDefinition<*>>()
                succeeded(type.deprecated)
            }
        }
        field<String> {
            name = "deprecationReason"
        }

    }

    val __EnumValue = newObject {
        name = "__EnumValue"
        field<String> {
            name = "name"
            type = GraphQLStringNonNull
        }
        field<String> {
            name = "description"
        }
        field<Boolean> {
            name = "isDeprecated"
            type = GraphQLNonNull(GraphQLBoolean)
            fetcher = { environment ->
                val enumValue = environment.source<GraphQLEnumValueDefinition>()
                succeeded(enumValue.deprecated)
            }
        }
        field<String> {
            name = "deprecationReason"
        }
    }

    private val fieldsFetcher: DataFetcher<List<GraphQLFieldDefinition<*>>> = { environment ->
        val promise = CompletableFuture<List<GraphQLFieldDefinition<*>>>()
        val type = environment.source<Any?>()
        val includeDeprecated = environment.argument<Boolean>("includeDeprecated") ?: true
        if (type is GraphQLFieldsContainer) {
            val fieldDefinitions = type.fieldDefinitions
            if (includeDeprecated) {
                promise.complete(fieldDefinitions)
            } else {
                promise.complete(fieldDefinitions.filter { !it.deprecated })
            }
        } else {
            promise.complete(null)
        }

        promise
    }

    private val interfacesFetcher: DataFetcher<List<GraphQLInterfaceType>> = { environment ->
        val type = environment.source<Any>()
        succeeded(if (type is GraphQLObjectType) {
            type.interfaces
        } else {
            null
        })
    }

    private val possibleTypesFetcher: DataFetcher<List<GraphQLObjectType>> = { environment ->
        val promise = CompletableFuture<List<GraphQLObjectType>>()
        val type = environment.source<Any>()
        if (type is GraphQLInterfaceType) {
            promise.complete(SchemaUtil().findImplementations(environment.graphQLSchema, type))
        } else if (type is GraphQLUnionType) {
            promise.complete(type.types())
        } else {
            promise.complete(null)
        }
        promise
    }

    private val enumValuesTypesFetcher: DataFetcher<List<GraphQLEnumValueDefinition>> = { environment ->
        val promise = CompletableFuture<List<GraphQLEnumValueDefinition>>()
        val type = environment.source<Any>()
        val includeDeprecated = environment.argument<Boolean>("includeDeprecated") ?: true
        if (type is GraphQLEnumType) {
            val values = type.values
            if (includeDeprecated) {
                promise.complete(values)
            } else {
                promise.complete(values.filterNot { it.deprecated })
            }
        } else {
            promise.complete(null)
        }
        promise
    }

    private val inputFieldsFetcher: DataFetcher<List<GraphQLInputObjectField>> = { environment ->
        val type = environment.source<Any>()
        if (type is GraphQLInputObjectType) {
            completedFuture(type.fields)
        } else {
            completedFuture<List<GraphQLInputObjectField>>(null)
        }
    }

    private val ofTypeFetcher: DataFetcher<GraphQLType> = { environment ->
        val type = environment.source<Any>()
        if (type is GraphQLList) {
            completedFuture(type.wrappedType)
        } else if (type is GraphQLNonNull) {
            completedFuture(type.wrappedType)
        } else {
            completedFuture<GraphQLType>(null)
        }
    }


    val __Type = newObject {
        name = "__Type"
        field<TypeKind> {
            name = "kind"
            type = GraphQLNonNull(__TypeKind)
            fetcher = kindDataFetcher
        }
        field<String> {
            name = "description"
        }
        field<List<GraphQLFieldDefinition<*>>> {
            name = "fields"
            type = GraphQLList(GraphQLNonNull(__Field))
            fetcher = fieldsFetcher
        }
        field<List<GraphQLInterfaceType>> {
            name = "interfaces"
            type = GraphQLList(GraphQLNonNull(GraphQLTypeReference("__Type")))
            fetcher = interfacesFetcher
        }
        field<List<GraphQLObjectType>> {
            name = "possibleTypes"
            type = GraphQLList(GraphQLNonNull(GraphQLTypeReference("__Type")))
            fetcher = possibleTypesFetcher
        }
        field<List<GraphQLEnumValueDefinition>> {
            name = "enumValues"
            type = GraphQLList(GraphQLNonNull(__EnumValue))
            argument {
                name = "includeDeprecated"
                type = GraphQLBoolean
                defaultValue = false
            }
            fetcher = enumValuesTypesFetcher
        }
        field<List<GraphQLInputObjectField>> {
            name = "inputFields"
            type = GraphQLList(GraphQLNonNull(__InputValue))
            fetcher = inputFieldsFetcher
        }
        field<GraphQLType> {
            name = "ofType"
            type = GraphQLTypeReference("__Type")
            fetcher = ofTypeFetcher
        }
    }

    enum class DirectiveLocation {
        QUERY,
        MUTATION,
        FIELD,
        FRAGMENT_DEFINITION,
        FRAGMENT_SPREAD,
        INLINE_FRAGMENT
    }

    val __DirectiveLocation = newEnum {
        name = "__DirectiveLocation"
        description = "An enum describing valid locations where a directive can be placed"
        value {
            name = "QUERY"
            description = "Indicates the directive is valid on queries."
            value = DirectiveLocation.QUERY
        }
        value {
            name = "MUTATION"
            description = "Indicates the directive is valid on mutations."
            value = DirectiveLocation.MUTATION
        }
        value {
            name = "FIELD"
            description = "Indicates the directive is valid on fields."
            value = DirectiveLocation.FIELD
        }
        value {
            name = "FRAGMENT_DEFINITION"
            description = "Indicates the directive is valid on fragment definitions."
            value = DirectiveLocation.FRAGMENT_DEFINITION
        }
        value {
            name = "FRAGMENT_SPREAD"
            description = "Indicates the directive is valid on fragment spreads."
            value = DirectiveLocation.FRAGMENT_SPREAD
        }
        value {
            name = "INLINE_FRAGMENT"
            description = "Indicates the directive is valid on inline fragments."
            value = DirectiveLocation.INLINE_FRAGMENT
        }
    }

    val __Directive = newObject {
        name = "__Directive"
        field<String> { name = "name" }
        field<String> { name = "description" }
        field<List<DirectiveLocation>> {
            name = "locations"
            type = GraphQLList(GraphQLNonNull(__DirectiveLocation))
        }
        field<List<GraphQLArgument>> {
            name = "args"
            type = GraphQLNonNull(GraphQLList(GraphQLNonNull(__InputValue)))
            fetcher = { completedFuture(it.source<GraphQLDirective>().arguments) }
        }
        field<Boolean> {
            name = "onOperation"
            deprecationReason = "Use `locations`."
        }
        field<Boolean> {
            name = "onFragmention"
            deprecationReason = "Use `locations`."
        }
        field<Boolean> {
            name = "onField"
            deprecationReason = "Use `locations`."
        }
    }

    val __Schema = newObject {
        name = "__Schema"
        description = """A GraphQL Introspection defines the capabilities
                of a GraphQL server. It exposes all available types and directives on
                the server, the entry points for query, mutation, and subscription operations."""
        field<List<GraphQLType>> {
            name = "types"
            description = "A list of all types supported by this server."
            type = GraphQLNonNull(GraphQLList(GraphQLNonNull(__Type)))
            fetcher = { environment ->
                completedFuture(environment.source<GraphQLSchema>().allTypesAsList)
            }

        }
        field<GraphQLObjectType> {
            name = "queryType"
            description = "The type that query operations will be rooted at."
            type = GraphQLNonNull(__Type)
            fetcher = { environment ->
                completedFuture(environment.source<GraphQLSchema>().queryType)
            }
        }
        field<GraphQLObjectType> {
            name = "mutationType"
            description = "If this server supports mutation, the type that mutation operations will be rooted at."
            type = __Type
            fetcher = { environment ->
                completedFuture(environment.source<GraphQLSchema>().mutationType)
            }
        }
        field<List<GraphQLDirective>> {
            name = "directives"
            description = "'A list of all directives supported by this server."
            type = GraphQLNonNull(GraphQLList(GraphQLNonNull(__Directive)))
            fetcher = { environment ->
                completedFuture(environment.graphQLSchema.directives)
            }
        }
        field<Any> {
            name = "subscriptionType"
            description = "'If this server support subscription, the type that subscription operations will be rooted at."
            type = __Type
            fetcher { completedFuture(null) }
        }
    }
    val SchemaMetaFieldDef = newField<GraphQLSchema> {
        name = "__schema"
        description = "Access the current type schema of this server."
        type = GraphQLNonNull(__Schema)
        fetcher = { environment -> completedFuture(environment.graphQLSchema) }
    }

    val TypeMetaFieldDef = newField<GraphQLType> {
        name = "__type"
        description = "Request the type information of a single type."
        type = __Type
        argument {
            name = "name"
            type = GraphQLNonNull(GraphQLString)
        }
        fetcher = { environment ->
            val name = environment.argument<String>("name")!!
            completedFuture<GraphQLType>(environment.graphQLSchema.type(name))
        }
    }

    val TypeNameMetaFieldDef = newField<String> {
        name = "__typename"
        description = "The name of the current Object type at runtime."
        type = GraphQLNonNull(GraphQLString)
        fetcher = { environment -> completedFuture(environment.parentType.name) }
    }

    init {
        // make sure all TypeReferences are resolved
        newSchema {
            query = newObject {
                name = "dummySchema"
                fields += SchemaMetaFieldDef
                fields += TypeMetaFieldDef
                fields += TypeNameMetaFieldDef
            }
        }
    }
}
