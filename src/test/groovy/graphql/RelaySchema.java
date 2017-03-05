package graphql;

import graphql.relay.Relay;
import graphql.schema.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public class RelaySchema
{

    public static Relay relay = new Relay();
    public static GraphQLObjectType StuffType = Companion.newObject()
                                                         .name("Stuff")
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("id")
                                                                         .type(INSTANCE.getGraphQLString())
                                                                         .fetchField())
                                                         .build();

    public static GraphQLInterfaceType NodeInterface = relay.nodeInterface(new TypeResolver()
    {
        @Override
        public GraphQLObjectType getType(Object object)
        {
            Relay.ResolvedGlobalId resolvedGlobalId = relay.fromGlobalId((String)object);
            //TODO: implement
            return null;
        }
    });

    public static GraphQLObjectType StuffEdgeType = relay
        .edgeType("Stuff", StuffType, null, new ArrayList<>());

    public static GraphQLObjectType StuffConnectionType = relay
        .connectionType("Stuff", StuffEdgeType, new ArrayList<>());

    public static GraphQLObjectType ThingType = Companion.newObject()
                                                         .name("Thing")
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("id")
                                                                         .type(INSTANCE.getGraphQLString())
                                                                         .fetchField())
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("stuffs")
                                                                         .type(StuffConnectionType))
                                                         .build();


    public static GraphQLObjectType RelayQueryType = Companion.newObject()
                                                              .name("RelayQuery")
                                                              .field(relay.nodeField(NodeInterface, environment -> {
            //TODO: implement
            return CompletableFuture.completedFuture((String)null);
        }))
                                                              .field(Companion.newFieldDefinition()
                                                                              .name("thing")
                                                                              .type(ThingType)
                                                                              .argument(Companion.newArgument()
                                      .name("id")
                                      .description("id of the thing")
                                      .type(new GraphQLNonNull(INSTANCE.getGraphQLString())))
                                                                              .dataFetcher(new DataFetcher()
                   {
                       @Override
                       public CompletionStage<Object> fetch(DataFetchingEnvironment environment)
                       {
                           //TODO: implement
                           return CompletableFuture.completedFuture(null);
                       }
                   }))
                                                              .build();


    public static GraphQLSchema Schema = GraphQLSchema.Companion.newSchema()
                                                                .query(RelayQueryType)
                                                                .build();
}
