package graphql


import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLObjectType.Companion.newObject

import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

object ScalarsQuerySchema {

    val inputDF: DataFetcher<Any> = { env ->
        CompletableFuture.completedFuture(env.argument("input"))
    }


    val queryType = newObject()
            .name("QueryType")
            /** Static Scalars  */
            .field(newFieldDefinition<Any>()
                           .name("bigInteger")
                           .type(GraphQLBigInteger)
                           .staticValue(BigInteger.valueOf(9999)))
            .field(newFieldDefinition<Any>()
                           .name("bigDecimal")
                           .type(GraphQLBigDecimal)
                           .staticValue(BigDecimal.valueOf(1234.0)))
            .field(newFieldDefinition<Any>()
                           .name("floatNaN")
                           .type(GraphQLFloat)
                           .staticValue(java.lang.Double.NaN))


            /** Scalars with input of same type, value echoed back  */
            .field(newFieldDefinition<Any>()
                           .name("bigIntegerInput")
                           .type(GraphQLBigInteger)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLNonNull(GraphQLBigInteger)))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("bigDecimalInput")
                           .type(GraphQLBigDecimal)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLNonNull(GraphQLBigDecimal)))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("floatNaNInput")
                           .type(GraphQLFloat)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLNonNull(GraphQLFloat)))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("stringInput")
                           .type(GraphQLString)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLNonNull(GraphQLString)))
                           .fetcher(inputDF))


            /** Scalars with input of String, cast to scalar  */
            .field(newFieldDefinition<Any>()
                           .name("bigIntegerString")
                           .type(GraphQLBigInteger)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("bigDecimalString")
                           .type(GraphQLBigDecimal)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("floatString")
                           .type(GraphQLFloat)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("longString")
                           .type(GraphQLLong)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("intString")
                           .type(GraphQLInt)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("shortString")
                           .type(GraphQLShort)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .field(newFieldDefinition<Any>()
                           .name("byteString")
                           .type(GraphQLByte)
                           .argument(newArgument()
                                             .name("input")
                                             .type(GraphQLString))
                           .fetcher(inputDF))
            .build()


    val scalarsQuerySchema = GraphQLSchema.newSchema()
            .query(queryType)
            .build()
}
