package graphql


import graphql.schema.newObject
import graphql.schema.newSchema
import org.junit.Assert.assertEquals
import org.junit.Test

class HelloWorld {

    @Test
    fun helloWorldTest() {
        val queryType = newObject {
            name = "helloWorldQuery"
            field<String> {
                name = "hello"
                staticValue = "world"
            }
        }

        val graphQL = newGraphQL {
            schema = newSchema {
                query = queryType
            }
        }
        val result = graphQL.execute("{hello}").toCompletableFuture().get().data<Map<String, Any?>>()

        assertEquals("world", result["hello"])
    }

}

fun main(args: Array<String>) {
    val queryType = newObject {
        name = "helloWorldQuery"
        field<String> {
            name = "hello"
            staticValue = "world"
        }
    }

    val graphQL = newGraphQL {
        this.schema = newSchema { query = queryType }
    }

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
