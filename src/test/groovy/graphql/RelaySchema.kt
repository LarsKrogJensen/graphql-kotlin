package graphql

import graphql.relay.connectionType
import graphql.relay.edgeType
import graphql.relay.nodeField
import graphql.relay.nodeInterface
import graphql.schema.GraphQLNonNull
import graphql.schema.newObject
import graphql.schema.newSchema
import java.util.concurrent.CompletableFuture


val NodeInterface = nodeInterface { null }
val STUFF = "Stuff"

val StuffConnectionType = connectionType<Any> {
    baseName = STUFF
    edgeType = edgeType<Any> {
        baseName = STUFF
        nodeType = newObject {
            name = STUFF
            field<String> {
                name = "id"
                isField = true
            }
        }
    }
}

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
    field(nodeField(NodeInterface) {
        CompletableFuture.completedFuture<String>(null as String?)
    })
    field<String> {
        name = "thing"
        type = ThingType
        argument {
            name = "id"
            description = "id of the thing"
            type = GraphQLNonNull(GraphQLString)

        }
        fetcher = {
            CompletableFuture.completedFuture(null)
        }
    }
}

val Schema = newSchema {
    query = RelayQueryType
}
