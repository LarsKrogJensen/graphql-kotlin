package graphql

import graphql.schema.*
import java.util.concurrent.CompletableFuture


object NestedInputSchema {

    fun createSchema(): GraphQLSchema {


        val root = rootType()

        return GraphQLSchema.newSchema()
                .query(root)
                .build()
    }

    fun rootType(): GraphQLObjectType {
        return GraphQLObjectType.newObject()

                .name("Root")
                .field(GraphQLFieldDefinition.newFieldDefinition<Any>()
                               .name("value")
                               .type(GraphQLInt)
                               .fetcher { environment ->

                                   val initialValue = environment.argument<Int>("initialValue")!!
                                   val filter = environment.argument<Map<String, Any>>("filter")!!

                                   if (filter.containsKey("even")) {
                                       val even = filter["even"] as Boolean
                                       if (even && initialValue % 2 != 0) {
                                           CompletableFuture.completedFuture<Any>(0)
                                       } else if (!even && initialValue % 2 == 0) {
                                           CompletableFuture.completedFuture<Any>(0)
                                       }
                                   }
                                   if (filter.containsKey("range")) {
                                       val range = filter["range"] as Map<String, Int>
                                       if (initialValue < range["lowerBound"]!! || initialValue > range["upperBound"]!!) {
                                           CompletableFuture.completedFuture<Any>(0)
                                       }
                                   }

                                   CompletableFuture.completedFuture<Any>(initialValue)
                               }
                               .argument(newArgument()
                                                 .name("intialValue")
                                                 .type(GraphQLInt)
                                                 .defaultValue(5))
                               .argument(newArgument()
                                                 .name("filter")
                                                 .type(filterType())))
                .build()
    }

    fun filterType(): GraphQLInputObjectType {
        return GraphQLInputObjectType.newInputObject()
                .name("Filter")
                .field(GraphQLInputObjectField.newInputObjectField()
                               .name("even")
                               .type(GraphQLBoolean))
                .field(GraphQLInputObjectField.newInputObjectField()
                               .name("range")
                               .type(rangeType()))
                .build()
    }

    fun rangeType(): GraphQLInputObjectType {
        return GraphQLInputObjectType.newInputObject()
                .name("Range")
                .field(GraphQLInputObjectField.newInputObjectField()
                               .name("lowerBound")
                               .type(GraphQLInt))
                .field(GraphQLInputObjectField.newInputObjectField()
                               .name("upperBound")
                               .type(GraphQLInt))
                .build()
    }
}
