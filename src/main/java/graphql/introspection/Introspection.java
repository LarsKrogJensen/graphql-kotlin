package graphql.introspection;


import graphql.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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

    public static GraphQLEnumType __TypeKind = GraphQLEnumType.newEnum()
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

    public static DataFetcher kindDataFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            CompletableFuture<Object> promise = new CompletableFuture<>();
            Object type = environment.getSource();
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
        }
    };

    public static GraphQLObjectType __InputValue = newObject()
            .name("__InputValue")
            .field(newFieldDefinition()
                    .name("name")
                    .type(new GraphQLNonNull(GraphQLString)))
            .field(newFieldDefinition()
                    .name("description")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("type")
                    .type(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
            .field(newFieldDefinition()
                    .name("defaultValue")
                    .type(GraphQLString)
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            CompletableFuture<Object> promise = new CompletableFuture<>();
                            if (environment.getSource() instanceof GraphQLArgument) {
                                GraphQLArgument inputField = (GraphQLArgument) environment.getSource();
                                promise.complete(inputField.getDefaultValue() != null ? inputField.getDefaultValue().toString() : null);
                            } else if (environment.getSource() instanceof GraphQLInputObjectField) {
                                GraphQLInputObjectField inputField = (GraphQLInputObjectField) environment.getSource();
                                promise.complete(inputField.getDefaultValue() != null ? inputField.getDefaultValue().toString() : null);
                            } else {
                                promise.complete(null);
                            }

                            return promise;
                        }
                    }))
            .build();


    public static GraphQLObjectType __Field = newObject()
            .name("__Field")
            .field(newFieldDefinition()
                    .name("name")
                    .type(new GraphQLNonNull(GraphQLString)))
            .field(newFieldDefinition()
                    .name("description")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("args")
                    .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__InputValue))))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            CompletableFuture<Object> promise = new CompletableFuture<>();
                            Object type = environment.getSource();
                            promise.complete(((GraphQLFieldDefinition) type).getArguments());
                            return promise;
                        }
                    }))
            .field(newFieldDefinition()
                    .name("type")
                    .type(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
            .field(newFieldDefinition()
                    .name("isDeprecated")
                    .type(new GraphQLNonNull(GraphQLBoolean))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            CompletableFuture<Object> promise = new CompletableFuture<>();
                            Object type = environment.getSource();
                            promise.complete(((GraphQLFieldDefinition) type).isDeprecated());
                            return promise;
                        }
                    }))
            .field(newFieldDefinition()
                    .name("deprecationReason")
                    .type(GraphQLString))
            .build();


    public static GraphQLObjectType __EnumValue = newObject()
            .name("__EnumValue")
            .field(newFieldDefinition()
                    .name("name")
                    .type(new GraphQLNonNull(GraphQLString)))
            .field(newFieldDefinition()
                    .name("description")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("isDeprecated")
                    .type(new GraphQLNonNull(GraphQLBoolean))
                    .dataFetcher((DataFetcher)environment -> {
                        CompletableFuture<Object> promise = new CompletableFuture<>();
                        GraphQLEnumValueDefinition enumValue = (GraphQLEnumValueDefinition) environment.getSource();
                        promise.complete(enumValue.isDeprecated());
                        return promise;
                    }))
            .field(newFieldDefinition()
                    .name("deprecationReason")
                    .type(GraphQLString))
            .build();

    public static DataFetcher fieldsFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            CompletableFuture<Object> promise = new CompletableFuture<>();
            Object type = environment.getSource();
            Boolean includeDeprecated = environment.<Boolean>getArgument("includeDeprecated");
            if (type instanceof GraphQLFieldsContainer) {
                GraphQLFieldsContainer fieldsContainer = (GraphQLFieldsContainer) type;
                List<GraphQLFieldDefinition> fieldDefinitions = fieldsContainer.getFieldDefinitions();
                if (includeDeprecated) {
                    promise.complete(fieldDefinitions);
                    return promise;
                }
                List<GraphQLFieldDefinition> filtered = new ArrayList<GraphQLFieldDefinition>(fieldDefinitions);
                for (GraphQLFieldDefinition fieldDefinition : fieldDefinitions) {
                    if (fieldDefinition.isDeprecated()) filtered.remove(fieldDefinition);
                }
                promise.complete(filtered);
                return promise;
            }
            promise.complete(null);
            return promise;
        }
    };

    public static DataFetcher interfacesFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            CompletableFuture<Object> promise = new CompletableFuture<>();
            Object type = environment.getSource();
            if (type instanceof GraphQLObjectType) {
                promise.complete(((GraphQLObjectType) type).getInterfaces());
                return promise;
            }
            promise.complete(null);
            return promise;
        }
    };

    public static DataFetcher possibleTypesFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            CompletableFuture<Object> promise = new CompletableFuture<>();
            Object type = environment.getSource();
            if (type instanceof GraphQLInterfaceType) {
                promise.complete(new SchemaUtil().findImplementations(environment.getGraphQLSchema(), (GraphQLInterfaceType) type));
                return promise;
            }
            if (type instanceof GraphQLUnionType) {
                promise.complete(((GraphQLUnionType) type).getTypes());
                return promise;
            }
            promise.complete(null);
            return promise;
        }
    };

    public static DataFetcher enumValuesTypesFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            CompletableFuture<Object> promise = new CompletableFuture<>();
            Object type = environment.getSource();
            Boolean includeDeprecated = environment.<Boolean>getArgument("includeDeprecated");
            if (type instanceof GraphQLEnumType) {
                List<GraphQLEnumValueDefinition> values = ((GraphQLEnumType) type).getValues();
                if (includeDeprecated) {
                    promise.complete(values);
                    return promise;
                }
                List<GraphQLEnumValueDefinition> filtered = new ArrayList<GraphQLEnumValueDefinition>(values);
                for (GraphQLEnumValueDefinition valueDefinition : values) {
                    if (valueDefinition.isDeprecated()) filtered.remove(valueDefinition);
                }
                promise.complete(filtered);
                return promise;
            }
            return CompletableFuture.completedFuture(null);
        }
    };

    public static DataFetcher inputFieldsFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            Object type = environment.getSource();
            if (type instanceof GraphQLInputObjectType) {
                return CompletableFuture.completedFuture(((GraphQLInputObjectType) type).getFields());
            }
            return CompletableFuture.completedFuture(null);
        }
    };

    public static DataFetcher OfTypeFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
            Object type = environment.getSource();
            if (type instanceof GraphQLList) {
                return CompletableFuture.completedFuture(((GraphQLList) type).getWrappedType());
            }
            if (type instanceof GraphQLNonNull) {
                return CompletableFuture.completedFuture(((GraphQLNonNull) type).getWrappedType());
            }
            return CompletableFuture.completedFuture(null);
        }
    };


    public static GraphQLObjectType __Type = newObject()
            .name("__Type")
            .field(newFieldDefinition()
                    .name("kind")
                    .type(new GraphQLNonNull(__TypeKind))
                    .dataFetcher(kindDataFetcher))
            .field(newFieldDefinition()
                    .name("name")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("description")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("fields")
                    .type(new GraphQLList(new GraphQLNonNull(__Field)))
                    .argument(newArgument()
                            .name("includeDeprecated")
                            .type(GraphQLBoolean)
                            .defaultValue(false))
                    .dataFetcher(fieldsFetcher))
            .field(newFieldDefinition()
                    .name("interfaces")
                    .type(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
                    .dataFetcher(interfacesFetcher))
            .field(newFieldDefinition()
                    .name("possibleTypes")
                    .type(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference("__Type"))))
                    .dataFetcher(possibleTypesFetcher))
            .field(newFieldDefinition()
                    .name("enumValues")
                    .type(new GraphQLList(new GraphQLNonNull(__EnumValue)))
                    .argument(newArgument()
                            .name("includeDeprecated")
                            .type(GraphQLBoolean)
                            .defaultValue(false))
                    .dataFetcher(enumValuesTypesFetcher))
            .field(newFieldDefinition()
                    .name("inputFields")
                    .type(new GraphQLList(new GraphQLNonNull(__InputValue)))
                    .dataFetcher(inputFieldsFetcher))
            .field(newFieldDefinition()
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

    public static GraphQLEnumType __DirectiveLocation = GraphQLEnumType.newEnum()
            .name("__DirectiveLocation")
            .description("An enum describing valid locations where a directive can be placed")
            .value("QUERY", DirectiveLocation.QUERY, "Indicates the directive is valid on queries.")
            .value("MUTATION", DirectiveLocation.MUTATION, "Indicates the directive is valid on mutations.")
            .value("FIELD", DirectiveLocation.FIELD, "Indicates the directive is valid on fields.")
            .value("FRAGMENT_DEFINITION", DirectiveLocation.FRAGMENT_DEFINITION, "Indicates the directive is valid on fragment definitions.")
            .value("FRAGMENT_SPREAD", DirectiveLocation.FRAGMENT_SPREAD, "Indicates the directive is valid on fragment spreads.")
            .value("INLINE_FRAGMENT", DirectiveLocation.INLINE_FRAGMENT, "Indicates the directive is valid on inline fragments.")
            .build();

    public static GraphQLObjectType __Directive = newObject()
            .name("__Directive")
            .field(newFieldDefinition()
                    .name("name")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("description")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("locations")
                    .type(new GraphQLList(new GraphQLNonNull(__DirectiveLocation))))
            .field(newFieldDefinition()
                    .name("args")
                    .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__InputValue))))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            GraphQLDirective directive = (GraphQLDirective) environment.getSource();
                            return CompletableFuture.completedFuture(directive.getArguments());
                        }
                    }))
            .field(newFieldDefinition()
                    .name("onOperation")
                    .type(GraphQLBoolean)
                    .deprecate("Use `locations`."))
            .field(newFieldDefinition()
                    .name("onFragment")
                    .type(GraphQLBoolean)
                    .deprecate("Use `locations`."))
            .field(newFieldDefinition()
                    .name("onField")
                    .type(GraphQLBoolean)
                    .deprecate("Use `locations`."))
            .build();

    public static GraphQLObjectType __Schema = newObject()
            .name("__Schema")
            .description("A GraphQL Introspection defines the capabilities" +
                    " of a GraphQL server. It exposes all available types and directives on " +
                    "the server, the entry points for query, mutation, and subscription operations.")
            .field(newFieldDefinition()
                    .name("types")
                    .description("A list of all types supported by this server.")
                    .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__Type))))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            GraphQLSchema schema = (GraphQLSchema) environment.getSource();
                            return CompletableFuture.completedFuture(schema.getAllTypesAsList());
                        }
                    }))
            .field(newFieldDefinition()
                    .name("queryType")
                    .description("The type that query operations will be rooted at.")
                    .type(new GraphQLNonNull(__Type))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            GraphQLSchema schema = (GraphQLSchema) environment.getSource();
                            return CompletableFuture.completedFuture(schema.getQueryType());
                        }
                    }))
            .field(newFieldDefinition()
                    .name("mutationType")
                    .description("If this server supports mutation, the type that mutation operations will be rooted at.")
                    .type(__Type)
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            GraphQLSchema schema = (GraphQLSchema) environment.getSource();
                            return CompletableFuture.completedFuture(schema.getMutationType());
                        }
                    }))
            .field(newFieldDefinition()
                    .name("directives")
                    .description("'A list of all directives supported by this server.")
                    .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(__Directive))))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            return CompletableFuture.completedFuture(environment.getGraphQLSchema().getDirectives());
                        }
                    }))
            .field(newFieldDefinition()
                    .name("subscriptionType")
                    .description("'If this server support subscription, the type that subscription operations will be rooted at.")
                    .type(__Type)
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            // Not yet supported
                            return CompletableFuture.completedFuture(null);
                        }
                    }))
            .build();


    public static GraphQLFieldDefinition SchemaMetaFieldDef = newFieldDefinition()
            .name("__schema")
            .type(new GraphQLNonNull(__Schema))
            .description("Access the current type schema of this server.")
            .dataFetcher(new DataFetcher() {
                @Override
                public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                    return CompletableFuture.completedFuture(environment.getGraphQLSchema());
                }
            }).build();

    public static GraphQLFieldDefinition TypeMetaFieldDef = newFieldDefinition()
            .name("__type")
            .type(__Type)
            .description("Request the type information of a single type.")
            .argument(newArgument()
                    .name("name")
                    .type(new GraphQLNonNull(GraphQLString)))
            .dataFetcher(new DataFetcher() {
                @Override
                public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                    String name = environment.<String>getArgument("name");
                    return CompletableFuture.completedFuture(environment.getGraphQLSchema().getType(name));
                }
            }).build();

    public static GraphQLFieldDefinition TypeNameMetaFieldDef = newFieldDefinition()
            .name("__typename")
            .type(new GraphQLNonNull(GraphQLString))
            .description("The name of the current Object type at runtime.")
            .dataFetcher(new DataFetcher() {
                @Override
                public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                    return CompletableFuture.completedFuture(environment.getParentType().getName());
                }
            })
            .build();


    static {
        // make sure all TypeReferences are resolved
        GraphQLSchema.newSchema()
                .query(GraphQLObjectType.newObject()
                        .name("dummySchema")
                        .field(SchemaMetaFieldDef)
                        .field(TypeMetaFieldDef)
                        .field(TypeNameMetaFieldDef)
                        .build())
                .build();
    }
}
