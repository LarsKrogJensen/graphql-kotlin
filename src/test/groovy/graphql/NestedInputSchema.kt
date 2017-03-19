package graphql

import graphql.schema.newInputObject
import graphql.schema.newObject
import graphql.schema.newSchema
import java.util.concurrent.CompletableFuture.completedFuture


val rangeType = newInputObject {
    name = "Range"
    field {
        name = "lowerBound"
        type = GraphQLInt
    }
    field {
        name = "upperBound"
        type = GraphQLInt
    }
}

val filterType = newInputObject {
    name = "Filter"
    field {
        name = "even"
        type = GraphQLBoolean
    }
    field {
        name = "range"
        type = rangeType
    }
}

val rootType = newObject {
    name = "Root"
    field<Int> {
        name = "value"
        argument {
            name = "initialValue"
            type = GraphQLInt
            defaultValue = 5
        }
        argument {
            name = "filter"
            type = filterType
        }
        fetcher = { environment ->
            val initialValue = environment.argument<Int>("initialValue")!!
            val filter = environment.argument<Map<String, Any>>("filter")!!

            if (filter.containsKey("even")) {
                val even = filter["even"] as Boolean
                if (even && initialValue % 2 != 0) {
                    completedFuture<Int>(0)
                } else if (!even && initialValue % 2 == 0) {
                    completedFuture<Int>(0)
                }
            } else if (filter.containsKey("range")) {
                val range = filter["range"] as Map<String, Int>
                if (initialValue < range["lowerBound"]!! || initialValue > range["upperBound"]!!) {
                    completedFuture<Int>(0)
                }
            }

            completedFuture<Int>(initialValue)
        }
    }
}
val nestedSchema = newSchema {
    query = rootType
}