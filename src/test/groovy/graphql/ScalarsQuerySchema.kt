package graphql


import graphql.schema.*

import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.CompletableFuture.completedFuture

private fun <T> inputFetcher(): DataFetcher<T> = {
    completedFuture(it.argument("input"))
}


private val scalarQueryType = newObject {
    name = "QueryType"

    /** Static Scalars  */
    field<BigInteger> {
        name = "bigInteger"
        staticValue = BigInteger.valueOf(9999)
    }
    field<BigDecimal> {
        name = "bigDecimal"
        staticValue = BigDecimal.valueOf(1234.0)
    }
    field<Double> {
        name = "floatNaN"
        staticValue = Double.NaN
    }

    /** Scalars with input of same type, value echoed back  */
    field<BigInteger> {
        name = "bigIntegerInput"
        argument {
            name = "input"
            type = GraphQLNonNull(GraphQLBigInteger)
        }
        fetcher = inputFetcher()
    }
    field<BigDecimal> {
        name = "bigDecimalInput"
        argument {
            name = "input"
            type = GraphQLNonNull(GraphQLBigDecimal)
        }
        fetcher = inputFetcher()
    }
    field<Double> {
        name = "floatNaNInput"
        argument {
            name = "input"
            type = GraphQLNonNull(GraphQLFloat)
        }
        fetcher = inputFetcher()
    }
    field<String> {
        name = "stringInput"
        argument {
            name = "input"
            type = GraphQLStringNonNull
        }
        fetcher = inputFetcher()
    }

    /** Scalars with input of String, cast to scalar  */
    field<BigInteger> {
        name = "bigIntegerString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
    field<BigDecimal> {
        name = "bigDecimalString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
    field<Double> {
        name = "floatString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
    field<Long> {
        name = "longString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
    field<Int> {
        name = "intString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
    field<Short> {
        name = "shortString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
    field<Byte> {
        name = "byteString"
        argument {
            name = "input"
            type = GraphQLString
        }
        fetcher = inputFetcher()
    }
}

val scalarsQuerySchema = newSchema {
    query = scalarQueryType
}
