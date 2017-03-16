package graphql


import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLSchema.Companion.newSchema
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

object MutationSchema {

    class NumberHolder(var theNumber: Int)

    class Root(number: Int) {
        internal var numberHolder: NumberHolder

        init {
            this.numberHolder = NumberHolder(number)
        }

        fun changeNumber(newNumber: Int): NumberHolder {
            this.numberHolder.theNumber = newNumber
            return this.numberHolder
        }


        fun failToChangeTheNumber(newNumber: Int): NumberHolder {
            throw RuntimeException("Cannot change the number")
        }


    }

    var numberHolderType = GraphQLObjectType.newObject()
            .name("NumberHolder")
            .field(newFieldDefinition<Int>()
                           .name("theNumber")
                           .type(GraphQLInt))
            .build()

    val queryType = GraphQLObjectType.newObject()
            .name("queryType")
            .field(newFieldDefinition<Int>()
                           .name("numberHolder")
                           .type(numberHolderType))
            .build()

    val mutationType = GraphQLObjectType.newObject()
            .name("mutationType")
            .field(newFieldDefinition<Any>()
                           .name("changeTheNumber")
                           .type(numberHolderType)
                           .argument(newArgument()
                                             .name("newNumber")
                                             .type(GraphQLInt))
                           .fetcher { environment ->
                               val newNumber = environment.argument<Int>("newNumber")!!
                               val root = environment.source<Any>() as Root
                               CompletableFuture.completedFuture<Any>(root.changeNumber(newNumber))
                           })
            .field(newFieldDefinition<Any>()
                           .name("failToChangeTheNumber")
                           .type(numberHolderType)
                           .argument(newArgument()
                                             .name("newNumber")
                                             .type(GraphQLInt))
                           .fetcher { environment ->
                               val newNumber = environment.argument<Int>("newNumber")!!
                               val root = environment.source<Any>() as Root
                               val promise = CompletableFuture<Any>()
                               try {
                                   promise.complete(root.failToChangeTheNumber(newNumber))
                               } catch (e: Exception) {
                                   promise.completeExceptionally(e)
                               }

                               promise
                           })
            .build()

    val schema = newSchema {
        query = queryType
        mutation = mutationType
    }
}
