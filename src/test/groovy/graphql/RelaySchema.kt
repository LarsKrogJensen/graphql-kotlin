package graphql

import graphql.relay.Relay
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType.Companion.newObject
import graphql.schema.GraphQLSchema
import graphql.schema.newArgument
import java.util.*
import java.util.concurrent.CompletableFuture


object RelaySchema {

    var relay = Relay()
    var StuffType = newObject()
            .name("Stuff")
            .field(newFieldDefinition<String>()
                           .name("id")
                           .type(GraphQLString)
                           .fetchField())
            .build()

    var NodeInterface = relay.nodeInterface { null}

    var StuffEdgeType = relay
            .edgeType<Any>("Stuff", StuffType, ArrayList<GraphQLFieldDefinition<*>>())

    var StuffConnectionType = relay
            .connectionType<Any>("Stuff", StuffEdgeType, ArrayList<GraphQLFieldDefinition<*>>())

    var ThingType = newObject()
            .name("Thing")
            .field(newFieldDefinition<String>()
                           .name("id")
                           .type(GraphQLString)
                           .fetchField())
            .field(newFieldDefinition<String>()
                           .name("stuffs")
                           .type(StuffConnectionType))
            .build()


    var RelayQueryType = newObject()
            .name("RelayQuery")
            .field(relay.nodeField(NodeInterface) {
                CompletableFuture.completedFuture<String>(null as String?)
            })
            .field(newFieldDefinition<String>()
                           .name("thing")
                           .type(ThingType)
                           .argument(newArgument()
                                             .name("id")
                                             .description("id of the thing")
                                             .type(GraphQLNonNull(GraphQLString)))
                           .dataFetcher{
                               CompletableFuture.completedFuture(null)
                           })
            .build()


    var Schema = GraphQLSchema.newSchema()
            .query(RelayQueryType)
            .build()
}
