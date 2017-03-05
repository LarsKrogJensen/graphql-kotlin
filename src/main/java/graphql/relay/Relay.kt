package graphql.relay


import graphql.GraphQLBoolean
import graphql.GraphQLID
import graphql.GraphQLInt
import graphql.GraphQLString
import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLInputObjectField.Companion.newInputObjectField
import graphql.schema.GraphQLInputObjectType.Companion.newInputObject
import graphql.schema.GraphQLInterfaceType.Companion.newInterface
import graphql.schema.GraphQLObjectType.Companion.newObject


class Relay {
    private val pageInfoType = newObject()
            .name("PageInfo")
            .description("Information about pagination in a connection.")
            .field(newFieldDefinition<Boolean>()
                           .name("hasNextPage")
                           .type(GraphQLNonNull(GraphQLBoolean))
                           .description("When paginating forwards, are there more items?"))
            .field(newFieldDefinition<Boolean>()
                           .name("hasPreviousPage")
                           .type(GraphQLNonNull(GraphQLBoolean))
                           .description("When paginating backwards, are there more items?"))
            .field(newFieldDefinition<String>()
                           .name("startCursor")
                           .type(GraphQLString)
                           .description("When paginating backwards, the cursor to continue."))
            .field(newFieldDefinition<String>()
                           .name("endCursor")
                           .type(GraphQLString)
                           .description("When paginating forwards, the cursor to continue."))
            .build()

    fun nodeInterface(typeResolver: TypeResolver): GraphQLInterfaceType {
        val node = newInterface()
                .name(NODE)
                .description("An object with an ID")
                .typeResolver(typeResolver)
                .field(newFieldDefinition<String>()
                               .name("id")
                               .description("The ID of an object")
                               .type(GraphQLNonNull(GraphQLID)))
                .build()
        return node
    }

    fun <T> nodeField(nodeInterface: GraphQLInterfaceType, nodeDataFetcher: DataFetcher<T>): GraphQLFieldDefinition<*> {
        val fieldDefinition = newFieldDefinition<T>()
                .name("node")
                .description("Fetches an object given its ID")
                .type(nodeInterface)
                .dataFetcher(nodeDataFetcher)
                .argument(newArgument()
                                  .name("id")
                                  .description("The ID of an object")
                                  .type(GraphQLNonNull(GraphQLID)))
                .build()
        return fieldDefinition
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

    fun <T>edgeType(name: String,
                 nodeType: GraphQLOutputType,
                 nodeInterface: GraphQLInterfaceType,
                 edgeFields: List<GraphQLFieldDefinition<*>>): GraphQLObjectType {

        return newObject()
                .name(name + "Edge")
                .description("An edge in a connection.")
                .field(newFieldDefinition<T>()
                               .name("node")
                               .type(nodeType)
                               .description("The item at the end of the edge"))
                .field(newFieldDefinition<String>()
                               .name("cursor")
                               .type(GraphQLNonNull(GraphQLString))
                               .description(""))
                .fields(edgeFields)
                .build()
    }

    fun <T>connectionType(name: String,
                       edgeType: GraphQLObjectType,
                       connectionFields: List<GraphQLFieldDefinition<*>>): GraphQLObjectType {

        return newObject()
                .name(name + "Connection")
                .description("A connection to a list of items.")
                .field(newFieldDefinition<List<T>>()
                               .name("edges")
                               .type(GraphQLList(edgeType)))
                .field(newFieldDefinition<PageInfo>()
                               .name("pageInfo")
                               .type(GraphQLNonNull(pageInfoType)))
                .fields(connectionFields)
                .build()
    }


    fun <T>mutationWithClientMutationId(name: String, fieldName: String,
                                     inputFields: List<GraphQLInputObjectField>,
                                     outputFields: List<GraphQLFieldDefinition<*>>,
                                     dataFetcher: DataFetcher<T>): GraphQLFieldDefinition<*> {
        val inputObjectType = newInputObject()
                .name(name + "Input")
                .field(newInputObjectField()
                               .name("clientMutationId")
                               .type(GraphQLNonNull(GraphQLString)))
                .fields(inputFields)
                .build()
        val outputType = newObject()
                .name(name + "Payload")
                .field(newFieldDefinition<String>()
                               .name("clientMutationId")
                               .type(GraphQLNonNull(GraphQLString)))
                .fields(outputFields)
                .build()

        return newFieldDefinition<T>()
                .name(fieldName)
                .type(outputType)
                .argument(newArgument()
                                  .name("input")
                                  .type(GraphQLNonNull(inputObjectType)))
                .dataFetcher(dataFetcher)
                .build()
    }

    class ResolvedGlobalId(
            @Deprecated("use {@link #getType()}")
            var type: String,
            @Deprecated("use {@link #getId()}")
            var id: String)

    fun toGlobalId(type: String, id: String): String {
        return Base64.toBase64(type + ":" + id)
    }

    fun fromGlobalId(globalId: String): ResolvedGlobalId {
        val split = Base64.fromBase64(globalId).split(":".toRegex(), 2).toTypedArray()
        return ResolvedGlobalId(split[0], split[1])
    }

    companion object {

        val NODE = "Node"
    }
}
