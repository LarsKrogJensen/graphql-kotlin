package graphql


import graphql.schema.newObject
import graphql.schema.newSchema
import graphql.schema.newSubscriptionObject
import graphql.util.succeeded
import reactor.core.Disposable
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime


fun main(args: Array<String>) {

    val timeSource = Flux.interval(Duration.ofMillis(1000)).map { LocalDateTime.now() }.doOnSubscribe {
        println("onSubscribe")
    }.doOnCancel {
        println("onCancel")
    }

    val ts = ConnectableFlux.create<LocalDateTime> {

    }

    val instantType = newObject {
        name = "Instant"
        field<Int> {
            name = "hour"
            fetcher { env ->
                succeeded(env.source<LocalDateTime>().hour)
            }
        }
        field<Int> {
            name = "min"
            fetcher { env ->
                succeeded(env.source<LocalDateTime>().minute)
            }
        }
        field<Int> {
            name = "sec"
            fetcher { env ->
                succeeded(env.source<LocalDateTime>().second)
            }
        }
    }

    val subscriptionRoot = newSubscriptionObject {
        name = "timeSubscription"
        subscription<LocalDateTime> {
            name = "timeSub"
            type = instantType
            publisher {
                timeSource
            }
        }
    }

    val queryRoot = newObject {
        name = "timeQuery"
        field<LocalDateTime> {
            name = "timeGet"
            type = instantType
            fetcher {
                succeeded(LocalDateTime.now())
            }
        }
    }

    val graphQL = newGraphQL {
        this.schema = newSchema {
            query = queryRoot
            subscription = subscriptionRoot
        }
    }

    graphQL.execute("subscription S {ts1:timeSub { hour min sec } ts2:timeSub { hour min sec } }")
        .handle { result, ex ->
            if (result.errors.isEmpty()) {
                val data = result.data<Map<String, Flux<ExecutionResult>>>()

                val disposables = data.map { (field: String, flux: Flux<ExecutionResult>) ->
                    flux.subscribe(
                        { next -> println("${Thread.currentThread().name} $field: ${next.data<Any>()}") },
                        { ex -> println("Field $field error $ex") },
                        { println("Field $field completed") }
                    )
                }
                disposables
            } else {
                println(result.errors)
                emptyList()
            }
        }.thenAccept { disposables: List<Disposable> ->

        Thread.sleep(10_000)

        disposables.forEach {
            it.dispose()
            Thread.sleep(2_000)
        }
    }

    readLine()
}
