package graphql


import graphql.schema.newObject
import graphql.schema.newSchema
import graphql.schema.newSubscriptionObject
import graphql.util.failed
import graphql.util.succeeded
import reactor.core.publisher.EmitterProcessor
import java.util.concurrent.CompletableFuture


class NumberHolder(var theNumber: Int)

class NumberStore(number: Int) {
    val changeFeed: EmitterProcessor<String> = EmitterProcessor.create<String>()


    internal var numberHolder: NumberHolder = NumberHolder(number)

    fun closeFeed() {
        changeFeed.onComplete()
    }
    
    fun changeNumber(newNumber: Int): NumberHolder {
        this.numberHolder.theNumber = newNumber
        changeFeed.onNext("Alert client that number is now [$newNumber]")
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
            val root = environment.source<Any>() as NumberStore
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
            val root = environment.source<NumberStore>()
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
    field<String> {
        name = "changeNumberSubscribe"
        type = numberHolderType
        argument {
            name = "clientId"
            type = GraphQLInt
        }

        publisher { environment ->
            environment.source<NumberStore>().changeFeed
        }
    }

}

val schema = newSchema {
    query = numberQueryType
    mutation = numberMutationType
    subscription = numberSubscriptionType
}



