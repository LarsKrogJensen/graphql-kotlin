package graphql.relay


import graphql.GraphQLBoolean
import graphql.GraphQLID
import graphql.GraphQLInt
import graphql.GraphQLString
import graphql.schema.*
import kotlin.properties.Delegates


private val pageInfoType = newObject {
    name = "PageInfo"
    description = "Information about pagination in a connection."
    field<Boolean> {
        name = "hasNextPage"
        description = "When paginating forwards, are there more items?"
        type = GraphQLNonNull(GraphQLBoolean)
    }
    field<Boolean> {
        name = "hasPreviousPage"
        description = "When paginating backwards, are there more items?"
        type = GraphQLNonNull(GraphQLBoolean)
    }
    field<String> {
        name = "startCursor"
        description = "When paginating backwards, the cursor to continue."
    }
    field<String> {
        name = "endCursor"
        description = "When paginating forwards, the cursor to continue."
    }
}

internal fun nodeInterface(resolver: TypeResolver) = newInterface {
    name = "Node"
    description = "An object with an ID"
    typeResolver = resolver
    field<String> {
        name = "id"
        description = "The ID of an object"
        type = GraphQLNonNull(GraphQLID)
    }
}

internal inline fun <reified T : Any> nodeField(nodeInterface: GraphQLInterfaceType, noinline nodeDataFetcher: DataFetcher<T>): GraphQLFieldDefinition<*> {
    return newField<T> {
        name = "node"
        description = "Fetches an object given its ID"
        type = nodeInterface
        fetcher = nodeDataFetcher
        argument {
            name = "id"
            description = "The ID of an object"
            type = GraphQLNonNull(GraphQLID)
        }
    }
}

val connectionFieldArguments: List<GraphQLArgument>
    get() {
        return listOf(newArgument().name("before").type(GraphQLString).build(),
                      newArgument().name("after").type(GraphQLString).build(),
                      newArgument().name("first").type(GraphQLInt).build(),
                      newArgument().name("last").type(GraphQLInt).build())
    }

val backwardPaginationConnectionFieldArguments: List<GraphQLArgument>
    get() {
        return listOf(newArgument().name("before").type(GraphQLString).build(),
                      newArgument().name("last").type(GraphQLInt).build())
    }

val forwardPaginationConnectionFieldArguments: List<GraphQLArgument>
    get() {
        return listOf(newArgument().name("after").type(GraphQLString).build(),
                      newArgument().name("first").type(GraphQLInt).build())
    }

@GraphQLDslMarker
class EdgeTypeBuilder {
    var baseName: String by Delegates.notNull<String>()
    val fields: MutableList<GraphQLFieldDefinition<*>> = mutableListOf()
    var nodeType: GraphQLOutputType by Delegates.notNull<GraphQLOutputType>()

    inline fun <reified T : Any> field(block: GraphQLFieldDefinition.Builder<T>.() -> Unit) {
        this.fields += newField(block)
    }
}

inline fun <reified T : Any> edgeType(block: EdgeTypeBuilder.() -> Unit) : GraphQLObjectType {
    val builder = EdgeTypeBuilder()
    builder.block()

    return newObject {
            name = builder.baseName + "Edge"
            description = "An edge in a connection."
            field<T> {
                name = "node"
                description = "The item at the end of the edge"
                type = builder.nodeType
            }
            field<String> {
                name = "cursor"
                type = GraphQLNonNull(GraphQLString)
            }
            fields += builder.fields
        }
}


@GraphQLDslMarker
class ConnectionTypeBuilder {
    var baseName: String by Delegates.notNull<String>()
    var edgeType: GraphQLOutputType by Delegates.notNull<GraphQLOutputType>()
    val fields: MutableList<GraphQLFieldDefinition<*>> = mutableListOf()

    inline fun <reified T : Any> field(block: GraphQLFieldDefinition.Builder<T>.() -> Unit) {
        this.fields += newField(block)
    }

}

fun <T> connectionType(block: ConnectionTypeBuilder.() -> Unit): GraphQLObjectType {
    val builder = ConnectionTypeBuilder()
    builder.block()

    return newObject {
        name = builder.baseName + "Connection"
        description = "A connection to a list of items."
        field<List<T>> {
            name = "edges"
            type = GraphQLList(builder.edgeType)
        }
        field<PageInfo> {
            name = "pageInfo"
            type = GraphQLNonNull(pageInfoType)
        }
        fields += builder.fields
    }
}


//inline fun <reified T : Any> mutationWithClientMutationId(baseName: String,
//                                                          fieldName: String,
//                                                          inputFields: List<GraphQLInputObjectField>,
//                                                          outputFields: List<GraphQLFieldDefinition<*>>,
//                                                          noinline dataFetcher: DataFetcher<T>): GraphQLFieldDefinition<*> {
//    val inputObjectType = newInputObject {
//        name = baseName + "Input"
//        field {
//            name = "clientMutationId"
//            type = GraphQLNonNull(GraphQLString)
//        }
//        fields += inputFields
//
//    }
//    val outputType = newObject {
//        name = baseName + "Payload"
//        field<String> {
//            name = "clientMutationId"
//            type = GraphQLNonNull(GraphQLString)
//        }
//        fields += outputFields
//    }
//
//    return newField <T> {
//        name = fieldName
//        type = outputType
//        argument {
//            name = "input"
//            type = GraphQLNonNull(inputObjectType)
//        }
//        fetcher = dataFetcher
//    }
//}