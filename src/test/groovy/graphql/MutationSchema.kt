package graphql


import graphql.schema.*
import graphql.util.failed
import graphql.util.succeeded
import java.util.concurrent.CompletableFuture

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

private val numberHolderType = newObject {
    name = "NumberHolder"
    field<Int> {
        name = "theNumber"
    }
}

private val numberQueryType = newObject {
    name = "queryType"
    field<Any> {
        name = "numberHolder"
        type = numberHolderType
    }
}

private val mutationType = newObject {
    name = "mutationType"
    field<Any> {
        name = "changeTheNumber"
        type = numberHolderType
        argument {
            name = "newNumber"
            type = GraphQLInt
        }
        fetcher = { environment ->
            val newNumber = environment.argument<Int>("newNumber")!!
            val root = environment.source<Any>() as Root
            CompletableFuture.completedFuture<Any>(root.changeNumber(newNumber))
        }
    }
    field<Any> {
        name = "failToChangeTheNumber"
        type = numberHolderType
        argument {
            name = "newNumber"
            type = GraphQLInt
        }
        fetcher = { environment ->
            val newNumber = environment.argument<Int>("newNumber")!!
            val root = environment.source<Any>() as Root
            try {
                succeeded(root.failToChangeTheNumber(newNumber))
            } catch (e: Exception) {
                failed<Any>(e)
            }
        }
    }
}

val schema = newSchema {
    query = numberQueryType
    mutation = mutationType
}

