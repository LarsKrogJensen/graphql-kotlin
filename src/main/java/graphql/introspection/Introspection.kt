package graphql.introspection


import graphql.GraphQLBoolean
import graphql.GraphQLString
import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLObjectType.Companion.newObject
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

    var __TypeKind = GraphQLEnumType.newEnum()
            .name("__TypeKind")
            .description("An enum describing what kind of type a given __Type is")
            .value("SCALAR", TypeKind.SCALAR, "Indicates this type is a scalar.")
            .value("OBJECT", TypeKind.OBJECT, "Indicates this type is an object. `fields` and `interfaces` are valid fields.")
            .value("INTERFACE", TypeKind.INTERFACE, "Indicates this type is an interface. `fields` and `possibleTypes` are valid fields.")
            .value("UNION", TypeKind.UNION, "Indicates this type is a union. `possibleTypes` is a valid field.")
            .value("ENUM", TypeKind.ENUM, "Indicates this type is an enum. `enumValues` is a valid field.")
            .value("INPUT_OBJECT", TypeKind.INPUT_OBJECT, "Indicates this type is an input object. `inputFields` is a valid field.")
            .value("LIST", TypeKind.LIST, "Indicates this type is a list. `ofType` is a valid field.")
            .value("NON_NULL", TypeKind.NON_NULL, "Indicates this type is a non-null. `ofType` is a valid field.")
            .build()

    private val kindDataFetcher: DataFetcher<TypeKind> = { environment ->
        val promise = CompletableFuture<TypeKind>()
        val type = environment.source<Any>()
        when (type) {
            is GraphQLScalarType      -> promise.complete(TypeKind.SCALAR)
            is GraphQLObjectType      -> promise.complete(TypeKind.OBJECT)
            is GraphQLInterfaceType   -> promise.complete(TypeKind.INTERFACE)
            is GraphQLUnionType       -> promise.complete(TypeKind.UNION)
            is GraphQLEnumType        -> promise.complete(TypeKind.ENUM)
            is GraphQLInputObjectType -> promise.complete(TypeKind.INPUT_OBJECT)
            is GraphQLList            -> promise.complete(TypeKind.LIST)
            is GraphQLNonNull         -> promise.complete(TypeKind.NON_NULL)
            else                      -> throw RuntimeException("Unknown kind of type: " + type)
        }
        promise
    }

    val __InputValue = newObject()
            .name("__InputValue")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLNonNull(GraphQLString)))
            .field(newFieldDefinition<String>()
                           .name("description")
                           .type(GraphQLString))
            .field(newFieldDefinition<String>()
                           .name("type")
                           .type(GraphQLNonNull(GraphQLTypeReference("__Type"))))
            .field(newFieldDefinition<String>()
                           .name("defaultValue")
                           .type(GraphQLString)
                           .dataFetcher({ environment ->
                                            val promise = CompletableFuture<String>()
                                            val source: Any = environment.source()
                                            promise.complete(
                                                    if (source is GraphQLArgument) {
                                                        source.defaultValue?.toString()
                                                    } else if (source is GraphQLInputObjectField) {
                                                        source.defaultValue?.toString()
                                                    } else null)
                                            promise
                                        }))
            .build()


    val __Field = newObject()
            .name("__Field")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLNonNull(GraphQLString)))
            .field(newFieldDefinition<String>()
                           .name("description")
                           .type(GraphQLString))
            .field(GraphQLFieldDefinition.newFieldDefinition<List<GraphQLArgument>>()
                           .name("args")
                           .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(__InputValue))))
                           .dataFetcher { environment ->
                               val promise = CompletableFuture<List<GraphQLArgument>>()
                               val type = environment.source<Any>() as GraphQLFieldDefinition<*>
                               promise.complete(type.arguments)
                               promise
                           })
            .field(newFieldDefinition<Any>()
                           .name("type")
                           .type(GraphQLNonNull(GraphQLTypeReference("__Type"))))
            .field(GraphQLFieldDefinition.newFieldDefinition<Boolean>()
                           .name("isDeprecated")
                           .type(GraphQLNonNull(GraphQLBoolean))
                           .dataFetcher { environment ->
                               val promise = CompletableFuture<Boolean>()
                               val type = environment.source<Any>()
                               promise.complete((type as GraphQLFieldDefinition<*>).deprecated)
                               promise
                           })
            .field(newFieldDefinition<String>()
                           .name("deprecationReason")
                           .type(GraphQLString))
            .build()


    val __EnumValue = newObject()
            .name("__EnumValue")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLNonNull(GraphQLString)))
            .field(newFieldDefinition<String>()
                           .name("description")
                           .type(GraphQLString))
            .field(GraphQLFieldDefinition.newFieldDefinition<Boolean>()
                           .name("isDeprecated")
                           .type(GraphQLNonNull(GraphQLBoolean))
                           .dataFetcher { environment ->
                               val promise = CompletableFuture<Boolean>()
                               val enumValue = environment.source<Any>() as GraphQLEnumValueDefinition
                               promise.complete(enumValue.deprecated)
                               promise
                           })
            .field(newFieldDefinition<String>()
                           .name("deprecationReason")
                           .type(GraphQLString))
            .build()

    private val fieldsFetcher: DataFetcher<List<GraphQLFieldDefinition<*>>> = { environment ->
        val promise = CompletableFuture<List<GraphQLFieldDefinition<*>>>()
        val type = environment.source<Any>()
        val includeDeprecated = environment.argument<Boolean>("includeDeprecated")
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
        val promise = CompletableFuture<List<GraphQLInterfaceType>>()
        val type = environment.source<Any>()
        if (type is GraphQLObjectType) {
            promise.complete(type.interfaces)
        } else {
            promise.complete(null)
        }

        promise
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
        val includeDeprecated = environment.argument<Boolean>("includeDeprecated")
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

    private val OfTypeFetcher: DataFetcher<GraphQLType> = { environment ->
        val type = environment.source<Any>()
        if (type is GraphQLList) {
            completedFuture(type.wrappedType)
        } else if (type is GraphQLNonNull) {
            completedFuture(type.wrappedType)
        } else {
            completedFuture<GraphQLType>(null)
        }
    }


    val __Type = newObject()
            .name("__Type")
            .field(GraphQLFieldDefinition.newFieldDefinition<TypeKind>()
                           .name("kind")
                           .type(GraphQLNonNull(__TypeKind))
                           .dataFetcher(kindDataFetcher))
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<String>()
                           .name("description")
                           .type(GraphQLString))
            .field(newFieldDefinition<List<GraphQLFieldDefinition<*>>>()
                           .name("fields")
                           .type(GraphQLList(GraphQLNonNull(__Field)))
                           .argument(newArgument()
                                             .name("includeDeprecated")
                                             .type(GraphQLBoolean)
                                             .defaultValue(false))
                           .dataFetcher(fieldsFetcher))
            .field(GraphQLFieldDefinition.newFieldDefinition<List<GraphQLInterfaceType>>()
                           .name("interfaces")
                           .type(GraphQLList(GraphQLNonNull(GraphQLTypeReference("__Type"))))
                           .dataFetcher(interfacesFetcher))
            .field(GraphQLFieldDefinition.newFieldDefinition<List<GraphQLObjectType>>()
                           .name("possibleTypes")
                           .type(GraphQLList(GraphQLNonNull(GraphQLTypeReference("__Type"))))
                           .dataFetcher(possibleTypesFetcher))
            .field(GraphQLFieldDefinition.newFieldDefinition<List<GraphQLEnumValueDefinition>>()
                           .name("enumValues")
                           .type(GraphQLList(GraphQLNonNull(__EnumValue)))
                           .argument(newArgument()
                                             .name("includeDeprecated")
                                             .type(GraphQLBoolean)
                                             .defaultValue(false))
                           .dataFetcher(enumValuesTypesFetcher))
            .field(GraphQLFieldDefinition.newFieldDefinition<List<GraphQLInputObjectField>>()
                           .name("inputFields")
                           .type(GraphQLList(GraphQLNonNull(__InputValue)))
                           .dataFetcher(inputFieldsFetcher))
            .field(GraphQLFieldDefinition.newFieldDefinition<GraphQLType>()
                           .name("ofType")
                           .type(GraphQLTypeReference("__Type"))
                           .dataFetcher(OfTypeFetcher))
            .build()

    enum class DirectiveLocation {
        QUERY,
        MUTATION,
        FIELD,
        FRAGMENT_DEFINITION,
        FRAGMENT_SPREAD,
        INLINE_FRAGMENT
    }

    val __DirectiveLocation = GraphQLEnumType.newEnum()
            .name("__DirectiveLocation")
            .description("An enum describing valid locations where a directive can be placed")
            .value("QUERY", DirectiveLocation.QUERY, "Indicates the directive is valid on queries.")
            .value("MUTATION", DirectiveLocation.MUTATION, "Indicates the directive is valid on mutations.")
            .value("FIELD", DirectiveLocation.FIELD, "Indicates the directive is valid on fields.")
            .value("FRAGMENT_DEFINITION", DirectiveLocation.FRAGMENT_DEFINITION, "Indicates the directive is valid on fragment definitions.")
            .value("FRAGMENT_SPREAD", DirectiveLocation.FRAGMENT_SPREAD, "Indicates the directive is valid on fragment spreads.")
            .value("INLINE_FRAGMENT", DirectiveLocation.INLINE_FRAGMENT, "Indicates the directive is valid on inline fragments.")
            .build()

    val __Directive = newObject()
            .name("__Directive")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<String>()
                           .name("description")
                           .type(GraphQLString))
            .field(newFieldDefinition<String>()
                           .name("locations")
                           .type(GraphQLList(GraphQLNonNull(__DirectiveLocation))))
            .field(newFieldDefinition<List<GraphQLArgument>>()
                           .name("args")
                           .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(__InputValue))))
                           .dataFetcher({ environment ->
                                            completedFuture(environment.source<GraphQLDirective>().arguments)
                                        }))
            .field(newFieldDefinition<Boolean>()
                           .name("onOperation")
                           .type(GraphQLBoolean)
                           .deprecate("Use `locations`."))
            .field(newFieldDefinition<Boolean>()
                           .name("onFragment")
                           .type(GraphQLBoolean)
                           .deprecate("Use `locations`."))
            .field(newFieldDefinition<Boolean>()
                           .name("onField")
                           .type(GraphQLBoolean)
                           .deprecate("Use `locations`."))
            .build()

    val __Schema = newObject()
            .name("__Schema")
            .description("A GraphQL Introspection defines the capabilities" +
                                 " of a GraphQL server. It exposes all available types and directives on " +
                                 "the server, the entry points for query, mutation, and subscription operations.")
            .field(newFieldDefinition<List<GraphQLType>>()
                           .name("types")
                           .description("A list of all types supported by this server.")
                           .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(__Type))))
                           .dataFetcher({ environment ->
                                            completedFuture(environment.source<GraphQLSchema>().allTypesAsList)
                                        }))
            .field(newFieldDefinition<GraphQLObjectType>()
                           .name("queryType")
                           .description("The type that query operations will be rooted at.")
                           .type(GraphQLNonNull(__Type))
                           .dataFetcher({ environment ->
                                            completedFuture(environment.source<GraphQLSchema>().queryType)
                                        }))
            .field(newFieldDefinition<GraphQLObjectType>()
                           .name("mutationType")
                           .description("If this server supports mutation, the type that mutation operations will be rooted at.")
                           .type(__Type)
                           .dataFetcher({ environment ->
                                            completedFuture(environment.source<GraphQLSchema>().mutationType)
                                        }))
            .field(newFieldDefinition<List<GraphQLDirective>>()
                           .name("directives")
                           .description("'A list of all directives supported by this server.")
                           .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(__Directive))))
                           .dataFetcher({ environment ->
                                            completedFuture(environment.graphQLSchema.directives)
                                        }))
            .field(newFieldDefinition<Any>()
                           .name("subscriptionType")
                           .description("'If this server support subscription, the type that subscription operations will be rooted at.")
                           .type(__Type)
                           .dataFetcher({
                                            // Not yet supported
                                            completedFuture(null)
                                        }))
            .build()


    val SchemaMetaFieldDef = newFieldDefinition<GraphQLSchema>()
            .name("__schema")
            .type(GraphQLNonNull(__Schema))
            .description("Access the current type schema of this server.")
            .dataFetcher { environment -> completedFuture(environment.graphQLSchema) }
            .build()

    val TypeMetaFieldDef = newFieldDefinition<GraphQLType>()
            .name("__type")
            .type(__Type)
            .description("Request the type information of a single type.")
            .argument(newArgument()
                              .name("name")
                              .type(GraphQLNonNull(GraphQLString)))
            .dataFetcher({ environment ->
                             val name = environment.argument<String>("name")
                             completedFuture<GraphQLType>(environment.graphQLSchema.type(name))
                         }).build()

    val TypeNameMetaFieldDef = newFieldDefinition<String>()
            .name("__typename")
            .type(GraphQLNonNull(GraphQLString))
            .description("The name of the current Object type at runtime.")
            .dataFetcher { environment -> completedFuture(environment.parentType.name) }
            .build()


    init {
        // make sure all TypeReferences are resolved
        GraphQLSchema.newSchema()
                .query(GraphQLObjectType.newObject()
                               .name("dummySchema")
                               .field(SchemaMetaFieldDef)
                               .field(TypeMetaFieldDef)
                               .field(TypeNameMetaFieldDef)
                               .build())
                .build()
    }
}
