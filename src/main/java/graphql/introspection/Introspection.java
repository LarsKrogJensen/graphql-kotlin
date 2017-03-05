package graphql.introspection;


import graphql.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public class Introspection {

    public enum TypeKind {
        SCALAR,
        OBJECT,
        INTERFACE,
        UNION,
        ENUM,
        INPUT_OBJECT,
        LIST,
        NON_NULL
    }

    public static GraphQLEnumType __TypeKind =
            GraphQLEnumType.Companion.newEnum()
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
                                     .build();

    private static DataFetcher<TypeKind> kindDataFetcher = environment -> {
        CompletableFuture<TypeKind> promise = new CompletableFuture<>();
        Object type = environment.source();
        if (type instanceof GraphQLScalarType) {
            promise.complete(TypeKind.SCALAR);
            return promise;
        } else if (type instanceof GraphQLObjectType) {
            promise.complete(TypeKind.OBJECT);
            return promise;
        } else if (type instanceof GraphQLInterfaceType) {
            promise.complete(TypeKind.INTERFACE);
            return promise;
        } else if (type instanceof GraphQLUnionType) {
            promise.complete(TypeKind.UNION);
            return promise;
        } else if (type instanceof GraphQLEnumType) {
            promise.complete(TypeKind.ENUM);
            return promise;
        } else if (type instanceof GraphQLInputObjectType) {
            promise.complete(TypeKind.INPUT_OBJECT);
            return promise;
        } else if (type instanceof GraphQLList) {
            promise.complete(TypeKind.LIST);
            return promise;
        } else if (type instanceof GraphQLNonNull) {
            promise.complete(TypeKind.NON_NULL);
            return promise;
        } else {
            throw new RuntimeException("Unknown kind of type: " + type);
        }
    };

    public static GraphQLObjectType __InputValue = Companion.newObject()
                                                            .name("__InputValue")
                                                            .field(Companion.newFieldDefinition()
                                                                            .name("name")
                                                                            .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                                                            .field(Companion.newFieldDefinition()
                                                                            .name("description")
                                                                            .type(INSTANCE.getGraphQLString()))
                                                            .field(Companion.newFieldDefinition()
                                                                            .name("type")
                                                                            .type(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
                                                            .field(Companion.newFieldDefinition()
                                                                            .name("defaultValue")
                                                                            .type(INSTANCE.getGraphQLString())
                                                                            .dataFetcher(environment -> {
                        CompletableFuture<Object> promise = new CompletableFuture<>();
                        if (environment.source() instanceof GraphQLArgument) {
                            GraphQLArgument inputField = (GraphQLArgument) environment.source();
                            promise.complete(inputField.getDefaultValue() != null ? inputField.getDefaultValue().toString() : null);
                        } else if (environment.source() instanceof GraphQLInputObjectField) {
                            GraphQLInputObjectField inputField = (GraphQLInputObjectField) environment.source();
                            promise.complete(inputField.getDefaultValue() != null ? inputField.getDefaultValue().toString() : null);
                        } else {
                            promise.complete(null);
                        }

                        return promise;
                    }))
                                                            .build();


    public static GraphQLObjectType __Field = Companion.newObject()
                                                       .name("__Field")
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("name")
                                                                       .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("description")
                                                                       .type(INSTANCE.getGraphQLString()))
                                                       .field(GraphQLFieldDefinition.Companion.<List<GraphQLArgument>>newFieldDefinition()
                    .name("args")
                    .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__InputValue))))
                    .dataFetcher(environment -> {
                        CompletableFuture<List<GraphQLArgument>> promise = new CompletableFuture<>();
                        GraphQLFieldDefinition<?> type = (GraphQLFieldDefinition<?>) environment.source();
                        promise.complete(type.getArguments());
                        return promise;
                    }))
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("type")
                                                                       .type(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
                                                       .field(GraphQLFieldDefinition.Companion.<Boolean>newFieldDefinition()
                    .name("isDeprecated")
                    .type(new GraphQLNonNull(INSTANCE.getGraphQLBoolean()))
                    .dataFetcher(environment -> {
                        CompletableFuture<Boolean> promise = new CompletableFuture<>();
                        Object type = environment.source();
                        promise.complete(((GraphQLFieldDefinition) type).getDeprecated());
                        return promise;
                    }))
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("deprecationReason")
                                                                       .type(INSTANCE.getGraphQLString()))
                                                       .build();


    public static GraphQLObjectType __EnumValue = Companion.newObject()
                                                           .name("__EnumValue")
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("name")
                                                                           .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("description")
                                                                           .type(INSTANCE.getGraphQLString()))
                                                           .field(GraphQLFieldDefinition.Companion.<Boolean>newFieldDefinition()
                    .name("isDeprecated")
                    .type(new GraphQLNonNull(INSTANCE.getGraphQLBoolean()))
                    .dataFetcher(environment -> {
                        CompletableFuture<Boolean> promise = new CompletableFuture<>();
                        GraphQLEnumValueDefinition enumValue = (GraphQLEnumValueDefinition) environment.source();
                        promise.complete(enumValue.getDeprecated());
                        return promise;
                    }))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("deprecationReason")
                                                                           .type(INSTANCE.getGraphQLString()))
                                                           .build();

    private static DataFetcher<List<GraphQLFieldDefinition>> fieldsFetcher = environment -> {
        CompletableFuture<List<GraphQLFieldDefinition>> promise = new CompletableFuture<>();
        Object type = environment.source();
        Boolean includeDeprecated = environment.<Boolean>argument("includeDeprecated");
        if (type instanceof GraphQLFieldsContainer) {
            GraphQLFieldsContainer fieldsContainer = (GraphQLFieldsContainer) type;
            List<GraphQLFieldDefinition> fieldDefinitions = fieldsContainer.getFieldDefinitions();
            if (includeDeprecated) {
                promise.complete(fieldDefinitions);
                return promise;
            }
            List<GraphQLFieldDefinition> filtered = new ArrayList<>(fieldDefinitions);
            for (GraphQLFieldDefinition fieldDefinition : fieldDefinitions) {
                if (fieldDefinition.getDeprecated()) filtered.remove(fieldDefinition);
            }
            promise.complete(filtered);
            return promise;
        }
        promise.complete(null);
        return promise;
    };

    private static DataFetcher<List<GraphQLInterfaceType>> interfacesFetcher = environment -> {
        CompletableFuture<List<GraphQLInterfaceType>> promise = new CompletableFuture<>();
        Object type = environment.source();
        if (type instanceof GraphQLObjectType) {
            promise.complete(((GraphQLObjectType) type).interfaces());
            return promise;
        }
        promise.complete(null);
        return promise;
    };

    private static DataFetcher<List<GraphQLObjectType>> possibleTypesFetcher = environment -> {
        CompletableFuture<List<GraphQLObjectType>> promise = new CompletableFuture<>();
        Object type = environment.source();
        if (type instanceof GraphQLInterfaceType) {
            promise.complete(new SchemaUtil().findImplementations(environment.getGraphQLSchema(), (GraphQLInterfaceType) type));
            return promise;
        }
        if (type instanceof GraphQLUnionType) {
            promise.complete(((GraphQLUnionType) type).types());
            return promise;
        }
        promise.complete(null);
        return promise;
    };

    private static DataFetcher<List<GraphQLEnumValueDefinition>> enumValuesTypesFetcher = environment -> {
        CompletableFuture<List<GraphQLEnumValueDefinition>> promise = new CompletableFuture<>();
        Object type = environment.source();
        Boolean includeDeprecated = environment.<Boolean>argument("includeDeprecated");
        if (type instanceof GraphQLEnumType) {
            List<GraphQLEnumValueDefinition> values = ((GraphQLEnumType) type).getValues();
            if (includeDeprecated) {
                promise.complete(values);
                return promise;
            }
            List<GraphQLEnumValueDefinition> filtered = new ArrayList<>(values);
            for (GraphQLEnumValueDefinition valueDefinition : values) {
                if (valueDefinition.getDeprecated()) filtered.remove(valueDefinition);
            }
            promise.complete(filtered);
            return promise;
        }
        return CompletableFuture.completedFuture(null);
    };

    private static DataFetcher<List<GraphQLInputObjectField>> inputFieldsFetcher = environment -> {
        Object type = environment.source();
        if (type instanceof GraphQLInputObjectType) {
            return CompletableFuture.completedFuture(((GraphQLInputObjectType) type).getFields());
        }
        return CompletableFuture.completedFuture(null);
    };

    private static DataFetcher<GraphQLType> OfTypeFetcher = environment -> {
        Object type = environment.source();
        if (type instanceof GraphQLList) {
            return CompletableFuture.completedFuture(((GraphQLList) type).getWrappedType());
        }
        if (type instanceof GraphQLNonNull) {
            return CompletableFuture.completedFuture(((GraphQLNonNull) type).getWrappedType());
        }
        return CompletableFuture.completedFuture(null);
    };


    public static GraphQLObjectType __Type = Companion.newObject()
                                                      .name("__Type")
                                                      .field(GraphQLFieldDefinition.Companion.<TypeKind>newFieldDefinition()
                    .name("kind")
                    .type(new GraphQLNonNull(__TypeKind))
                    .dataFetcher(kindDataFetcher))
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("name")
                                                                      .type(INSTANCE.getGraphQLString()))
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("description")
                                                                      .type(INSTANCE.getGraphQLString()))
                                                      .field(GraphQLFieldDefinition.Companion.<List<GraphQLFieldDefinition>>newFieldDefinition()
                    .name("fields")
                    .type(new GraphQLList(new GraphQLNonNull(__Field)))
                    .argument(Companion.newArgument()
                                       .name("includeDeprecated")
                                       .type(INSTANCE.getGraphQLBoolean())
                                       .defaultValue(false))
                    .dataFetcher(fieldsFetcher))
                                                      .field(GraphQLFieldDefinition.Companion.<List<GraphQLInterfaceType>>newFieldDefinition()
                    .name("interfaces")
                    .type(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
                    .dataFetcher(interfacesFetcher))
                                                      .field(GraphQLFieldDefinition.Companion.<List<GraphQLObjectType>>newFieldDefinition()
                    .name("possibleTypes")
                    .type(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
                    .dataFetcher(possibleTypesFetcher))
                                                      .field(GraphQLFieldDefinition.Companion.<List<GraphQLEnumValueDefinition>>newFieldDefinition()
                    .name("enumValues")
                    .type(new GraphQLList(new GraphQLNonNull(__EnumValue)))
                    .argument(Companion.newArgument()
                                       .name("includeDeprecated")
                                       .type(INSTANCE.getGraphQLBoolean())
                                       .defaultValue(false))
                    .dataFetcher(enumValuesTypesFetcher))
                                                      .field(GraphQLFieldDefinition.Companion.<List<GraphQLInputObjectField>>newFieldDefinition()
                    .name("inputFields")
                    .type(new GraphQLList(new GraphQLNonNull(__InputValue)))
                    .dataFetcher(inputFieldsFetcher))
                                                      .field(GraphQLFieldDefinition.Companion.<GraphQLType>newFieldDefinition()
                    .name("ofType")
                    .type(new GraphQLTypeReference("__Type"))
                    .dataFetcher(OfTypeFetcher))
                                                      .build();

    public enum DirectiveLocation {
        QUERY,
        MUTATION,
        FIELD,
        FRAGMENT_DEFINITION,
        FRAGMENT_SPREAD,
        INLINE_FRAGMENT
    }

    public static GraphQLEnumType __DirectiveLocation =
            GraphQLEnumType.Companion.newEnum()
                                     .name("__DirectiveLocation")
                                     .description("An enum describing valid locations where a directive can be placed")
                                     .value("QUERY", DirectiveLocation.QUERY, "Indicates the directive is valid on queries.")
                                     .value("MUTATION", DirectiveLocation.MUTATION, "Indicates the directive is valid on mutations.")
                                     .value("FIELD", DirectiveLocation.FIELD, "Indicates the directive is valid on fields.")
                                     .value("FRAGMENT_DEFINITION", DirectiveLocation.FRAGMENT_DEFINITION, "Indicates the directive is valid on fragment definitions.")
                                     .value("FRAGMENT_SPREAD", DirectiveLocation.FRAGMENT_SPREAD, "Indicates the directive is valid on fragment spreads.")
                                     .value("INLINE_FRAGMENT", DirectiveLocation.INLINE_FRAGMENT, "Indicates the directive is valid on inline fragments.")
                                     .build();

    public static GraphQLObjectType __Directive = Companion.newObject()
                                                           .name("__Directive")
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("name")
                                                                           .type(INSTANCE.getGraphQLString()))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("description")
                                                                           .type(INSTANCE.getGraphQLString()))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("locations")
                                                                           .type(new GraphQLList(new GraphQLNonNull(__DirectiveLocation))))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("args")
                                                                           .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__InputValue))))
                                                                           .dataFetcher(environment -> {
                        GraphQLDirective directive = (GraphQLDirective) environment.source();
                        return CompletableFuture.completedFuture(directive.getArguments());
                    }))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("onOperation")
                                                                           .type(INSTANCE.getGraphQLBoolean())
                                                                           .deprecate("Use `locations`."))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("onFragment")
                                                                           .type(INSTANCE.getGraphQLBoolean())
                                                                           .deprecate("Use `locations`."))
                                                           .field(Companion.newFieldDefinition()
                                                                           .name("onField")
                                                                           .type(INSTANCE.getGraphQLBoolean())
                                                                           .deprecate("Use `locations`."))
                                                           .build();

    public static GraphQLObjectType __Schema = Companion.newObject()
                                                        .name("__Schema")
                                                        .description("A GraphQL Introspection defines the capabilities" +
                    " of a GraphQL server. It exposes all available types and directives on " +
                    "the server, the entry points for query, mutation, and subscription operations.")
                                                        .field(Companion.newFieldDefinition()
                                                                        .name("types")
                                                                        .description("A list of all types supported by this server.")
                                                                        .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__Type))))
                                                                        .dataFetcher(environment -> {
                        GraphQLSchema schema = (GraphQLSchema) environment.source();
                        return CompletableFuture.completedFuture(schema.getAllTypesAsList());
                    }))
                                                        .field(Companion.newFieldDefinition()
                                                                        .name("queryType")
                                                                        .description("The type that query operations will be rooted at.")
                                                                        .type(new GraphQLNonNull(__Type))
                                                                        .dataFetcher(environment -> {
                        GraphQLSchema schema = (GraphQLSchema) environment.source();
                        return CompletableFuture.completedFuture(schema.getQueryType());
                    }))
                                                        .field(Companion.newFieldDefinition()
                                                                        .name("mutationType")
                                                                        .description("If this server supports mutation, the type that mutation operations will be rooted at.")
                                                                        .type(__Type)
                                                                        .dataFetcher(environment -> {
                        GraphQLSchema schema = (GraphQLSchema) environment.source();
                        return CompletableFuture.completedFuture(schema.getMutationType());
                    }))
                                                        .field(Companion.newFieldDefinition()
                                                                        .name("directives")
                                                                        .description("'A list of all directives supported by this server.")
                                                                        .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__Directive))))
                                                                        .dataFetcher(environment -> CompletableFuture.completedFuture(environment.getGraphQLSchema().getDirectives())))
                                                        .field(Companion.newFieldDefinition()
                                                                        .name("subscriptionType")
                                                                        .description("'If this server support subscription, the type that subscription operations will be rooted at.")
                                                                        .type(__Type)
                                                                        .dataFetcher(environment -> {
                        // Not yet supported
                        return CompletableFuture.completedFuture(null);
                    }))
                                                        .build();


    public static GraphQLFieldDefinition<GraphQLSchema> SchemaMetaFieldDef =
            GraphQLFieldDefinition.Companion.<GraphQLSchema>newFieldDefinition()
                    .name("__schema")
                    .type(new GraphQLNonNull(__Schema))
                    .description("Access the current type schema of this server.")
                    .dataFetcher(environment -> CompletableFuture.completedFuture(environment.getGraphQLSchema()))
                    .build();

    public static GraphQLFieldDefinition<GraphQLType> TypeMetaFieldDef =
            GraphQLFieldDefinition.Companion.<GraphQLType>newFieldDefinition()
                    .name("__type")
                    .type(__Type)
                    .description("Request the type information of a single type.")
                    .argument(Companion.newArgument().name("name")
                                       .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                    .dataFetcher(environment -> {
                        String name = environment.argument("name");
                        return CompletableFuture.completedFuture(environment.getGraphQLSchema().type(name));
                    }).build();

    public static GraphQLFieldDefinition<String> TypeNameMetaFieldDef =
            GraphQLFieldDefinition.Companion.<String>newFieldDefinition()
                    .name("__typename")
                    .type(new GraphQLNonNull(INSTANCE.getGraphQLString()))
                    .description("The name of the current Object type at runtime.")
                    .dataFetcher(environment -> CompletableFuture.completedFuture(environment.getParentType().getName()))
                    .build();


    static {
        // make sure all TypeReferences are resolved
        GraphQLSchema.Companion.newSchema()
                               .query(GraphQLObjectType.Companion.newObject()
                                                       .name("dummySchema")
                                                       .field(SchemaMetaFieldDef)
                                                       .field(TypeMetaFieldDef)
                                                       .field(TypeNameMetaFieldDef)
                                                       .build())
                               .build();
    }
}
