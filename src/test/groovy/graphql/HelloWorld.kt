package graphql


import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLObjectType.Companion.newObject
import graphql.schema.GraphQLSchema
import org.junit.Assert.assertEquals
import org.junit.Test

class HelloWorld {

    @Test
    fun helloWorldTest() {
        val queryType = newObject()
                .name("helloWorldQuery")
                .field(newFieldDefinition<String>()
                               .type(GraphQLString)
                               .name("hello")
                               .staticValue("world"))
                .build()

        val schema = GraphQLSchema.newSchema()
                .query(queryType)
                .build()
        val graphQL = newGraphQL(schema).build()
        val result = graphQL.execute("{hello}").toCompletableFuture().get().data<Map<String, Any?>>()

        assertEquals("world", result["hello"])
    }

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val queryType = newObject()
                    .name("helloWorldQuery")
                    .field(newFieldDefinition<String>()
                                   .type(GraphQLString)
                                   .name("hello")
                                   .staticValue("world"))
                    .build()

            val schema = GraphQLSchema.newSchema()
                    .query(queryType)
                    .build()

            val graphQL = newGraphQL(schema).build()

            val result = graphQL.execute("{hello}").handle { result, ex ->
                ex?.printStackTrace()
                if (result.errors.isEmpty())
                    println(result.data<Any>())
                else
                    println(result.errors)
            }

            println(result)
            // Prints: {hello=world}
        }
    }
}
