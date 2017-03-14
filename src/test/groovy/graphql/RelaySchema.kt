package graphql

import graphql.relay.Relay
import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLObjectType.Companion.newObject
import java.util.*
import java.util.concurrent.CompletableFuture


val relay = Relay()
val StuffType = newObject {
    name = "Stuff"
    field<String> {
        name = "id"
        isField = true
    }
}

val NodeInterface = relay.nodeInterface { null }

val StuffEdgeType =
        relay.edgeType<Any>("Stuff", StuffType, ArrayList<GraphQLFieldDefinition<*>>())

val StuffConnectionType =
        relay.connectionType<Any>("Stuff", StuffEdgeType, ArrayList<GraphQLFieldDefinition<*>>())

val ThingType = newObject {
    name = "Thing"
    field<String> {
        name = "id"
        isField = true
    }
    field<String> {
        name = "stuffs"
        type = StuffConnectionType
    }
}

val RelayQueryType = newObject {
    name = "RelayQuery"
    field(relay.nodeField(NodeInterface) {
        CompletableFuture.completedFuture<String>(null as String?)
    })
    field<String> {
        name = "thing"
        type = ThingType
        argument {
            name = "id"
            description = "id of the thing"
            type = GraphQLNonNull(GraphQLString)
            argument {

            }
        }
        fetcher = {
            CompletableFuture.completedFuture(null)
        }
    }
}

val Schema = newSchema {
    query = RelayQueryType
}
