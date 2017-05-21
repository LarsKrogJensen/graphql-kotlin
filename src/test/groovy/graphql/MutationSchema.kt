package graphql


import graphql.schema.newObject
import graphql.schema.newSchema
import graphql.schema.newSubscriptionObject
import graphql.util.failed
import graphql.util.succeeded
import reactor.core.publisher.Flux
import java.util.*
import java.util.concurrent.CompletableFuture


class NumberHolder(var theNumber: Int)

class Root(number: Int) {
    internal var numberHolder: NumberHolder

    init {
        this.numberHolder = NumberHolder(number)
    }

    fun changeNumber(newNumber: Int): NumberHolder {
        this.numberHolder.theNumber = newNumber
        SubscriptionRoot.numberChanged(newNumber)
        return this.numberHolder
    }


    fun failToChangeTheNumber(newNumber: Int): NumberHolder {
        throw RuntimeException("Cannot change the number")
    }

}

class SubscriptionRoot(val root: Root) {

    fun changeNumberSubscribe(clientId: Int) {
        subscribers.add(clientId)
    }

    companion object {
        internal var result: MutableList<String> = ArrayList()
        internal var subscribers: MutableList<Int> = ArrayList()

        fun numberChanged(newNumber: Int) {
            for (subscriber in subscribers) {
                // for test purposes only, a true implementation of a subscription mechanism needs to consider
                // the format in which subscribers have requested their response and tailor it accordingly
                result.add("Alert client [$subscriber] that number is now [$newNumber]")
            }
        }

        fun getResult(): List<String> {
            return result
        }
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

private val numberMutationType = newObject {
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
            val root = environment.source<Root>()
            try {
                succeeded(root.failToChangeTheNumber(newNumber))
            } catch (e: Exception) {
                failed<Any>(e)
            }
        }
    }
}

private val numberSubscriptionType = newSubscriptionObject {
    name = "subscriptionType"
    field<NumberHolder> {
        name = "changeNumberSubscribe"
        type = numberHolderType
        argument {
            name = "clientId"
            type = GraphQLInt
        }

        publisher { environment ->
            val flux: Flux<NumberHolder> = Flux.create<NumberHolder> {
                val subscriptionRoot = environment.source<SubscriptionRoot>()
                it.next(subscriptionRoot.root.numberHolder)
                it.complete()
            }

            flux
        }
    }

}

val schema = newSchema {
    query = numberQueryType
    mutation = numberMutationType
    subscription = numberSubscriptionType
}



