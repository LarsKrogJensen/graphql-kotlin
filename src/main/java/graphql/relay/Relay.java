package graphql.relay;


import graphql.schema.*;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;

public class Relay
{

    public static final String NODE = "Node";
    private GraphQLObjectType pageInfoType = Companion.newObject()
                                                      .name("PageInfo")
                                                      .description("Information about pagination in a connection.")
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("hasNextPage")
                                                                      .type(new GraphQLNonNull(INSTANCE.getGraphQLBoolean()))
                                                                      .description("When paginating forwards, are there more items?"))
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("hasPreviousPage")
                                                                      .type(new GraphQLNonNull(INSTANCE.getGraphQLBoolean()))
                                                                      .description("When paginating backwards, are there more items?"))
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("startCursor")
                                                                      .type(INSTANCE.getGraphQLString())
                                                                      .description("When paginating backwards, the cursor to continue."))
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("endCursor")
                                                                      .type(INSTANCE.getGraphQLString())
                                                                      .description("When paginating forwards, the cursor to continue."))
                                                      .build();

    public GraphQLInterfaceType nodeInterface(TypeResolver typeResolver)
    {
        GraphQLInterfaceType node = Companion.newInterface()
                                             .name(NODE)
                                             .description("An object with an ID")
                                             .typeResolver(typeResolver)
                                             .field(Companion.newFieldDefinition()
                                                             .name("id")
                                                             .description("The ID of an object")
                                                             .type(new GraphQLNonNull(INSTANCE.getGraphQLID())))
                                             .build();
        return node;
    }

    public GraphQLFieldDefinition nodeField(GraphQLInterfaceType nodeInterface, DataFetcher nodeDataFetcher)
    {
        GraphQLFieldDefinition fieldDefinition = Companion.newFieldDefinition()
                                                          .name("node")
                                                          .description("Fetches an object given its ID")
                                                          .type(nodeInterface)
                                                          .dataFetcher(nodeDataFetcher)
                                                          .argument(Companion.newArgument()
                               .name("id")
                               .description("The ID of an object")
                               .type(new GraphQLNonNull(INSTANCE.getGraphQLID())))
                                                          .build();
        return fieldDefinition;
    }

    public List<GraphQLArgument> getConnectionFieldArguments()
    {
        List<GraphQLArgument> args = new ArrayList<GraphQLArgument>();

        args.add(Companion.newArgument()
                          .name("before")
                          .type(INSTANCE.getGraphQLString())
                          .build());
        args.add(Companion.newArgument()
                          .name("after")
                          .type(INSTANCE.getGraphQLString())
                          .build());
        args.add(Companion.newArgument()
                          .name("first")
                          .type(INSTANCE.getGraphQLInt())
                          .build());
        args.add(Companion.newArgument()
                          .name("last")
                          .type(INSTANCE.getGraphQLInt())
                          .build());
        return args;
    }

    public List<GraphQLArgument> getBackwardPaginationConnectionFieldArguments()
    {
        List<GraphQLArgument> args = new ArrayList<GraphQLArgument>();

        args.add(Companion.newArgument()
                          .name("before")
                          .type(INSTANCE.getGraphQLString())
                          .build());
        args.add(Companion.newArgument()
                          .name("last")
                          .type(INSTANCE.getGraphQLInt())
                          .build());
        return args;
    }

    public List<GraphQLArgument> getForwardPaginationConnectionFieldArguments()
    {
        List<GraphQLArgument> args = new ArrayList<GraphQLArgument>();

        args.add(Companion.newArgument()
                          .name("after")
                          .type(INSTANCE.getGraphQLString())
                          .build());
        args.add(Companion.newArgument()
                          .name("first")
                          .type(INSTANCE.getGraphQLInt())
                          .build());
        return args;
    }

    public GraphQLObjectType edgeType(String name,
                                      GraphQLOutputType nodeType,
                                      GraphQLInterfaceType nodeInterface,
                                      List<GraphQLFieldDefinition<?>> edgeFields)
    {

        GraphQLObjectType edgeType = Companion.newObject()
                                              .name(name + "Edge")
                                              .description("An edge in a connection.")
                                              .field(Companion.newFieldDefinition()
                                                              .name("node")
                                                              .type(nodeType)
                                                              .description("The item at the end of the edge"))
                                              .field(Companion.newFieldDefinition()
                                                              .name("cursor")
                                                              .type(new GraphQLNonNull(INSTANCE.getGraphQLString()))
                                                              .description(""))
                                              .fields(edgeFields)
                                              .build();
        return edgeType;
    }

    public GraphQLObjectType connectionType(String name,
                                            GraphQLObjectType edgeType,
                                            List<GraphQLFieldDefinition<?>> connectionFields)
    {

        GraphQLObjectType connectionType = Companion.newObject()
                                                    .name(name + "Connection")
                                                    .description("A connection to a list of items.")
                                                    .field(Companion.newFieldDefinition()
                                                                    .name("edges")
                                                                    .type(new GraphQLList(edgeType)))
                                                    .field(Companion.newFieldDefinition()
                                                                    .name("pageInfo")
                                                                    .type(new GraphQLNonNull(pageInfoType)))
                                                    .fields(connectionFields)
                                                    .build();
        return connectionType;
    }


    public GraphQLFieldDefinition mutationWithClientMutationId(String name, String fieldName,
                                                               List<GraphQLInputObjectField> inputFields,
                                                               List<GraphQLFieldDefinition<?>> outputFields,
                                                               DataFetcher dataFetcher)
    {
        GraphQLInputObjectType inputObjectType = Companion.newInputObject()
                                                          .name(name + "Input")
                                                          .field(Companion.newInputObjectField()
                            .name("clientMutationId")
                            .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                                                          .fields(inputFields)
                                                          .build();
        GraphQLObjectType outputType = Companion.newObject()
                                                .name(name + "Payload")
                                                .field(Companion.newFieldDefinition()
                                                                .name("clientMutationId")
                                                                .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                                                .fields(outputFields)
                                                .build();

        return Companion.newFieldDefinition()
                        .name(fieldName)
                        .type(outputType)
                        .argument(Companion.newArgument()
                               .name("input")
                               .type(new GraphQLNonNull(inputObjectType)))
                        .dataFetcher(dataFetcher)
                        .build();
    }

    public static class ResolvedGlobalId
    {

        public ResolvedGlobalId(String type, String id)
        {
            this.type = type;
            this.id = id;
        }

        /**
         * @deprecated use {@link #getType()}
         */
        @Deprecated
        public String type;
        /**
         * @deprecated use {@link #getId()}
         */
        @Deprecated
        public String id;

        public String getType()
        {
            return type;
        }

        public String getId()
        {
            return id;
        }
    }

    public String toGlobalId(String type, String id)
    {
        return Base64.toBase64(type + ":" + id);
    }

    public ResolvedGlobalId fromGlobalId(String globalId)
    {
        String[] split = Base64.fromBase64(globalId).split(":", 2);
        return new ResolvedGlobalId(split[0], split[1]);
    }
}
