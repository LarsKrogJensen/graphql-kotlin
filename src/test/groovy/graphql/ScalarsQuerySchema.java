package graphql;


import graphql.schema.*;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ScalarsQuerySchema {

    public static final DataFetcher inputDF = new DataFetcher() {
        @Override
        public CompletionStage<Object> fetch(DataFetchingEnvironment environment) {
            return CompletableFuture.completedFuture(environment.argument("input"));
        }
    };

    public static final GraphQLObjectType queryType = Companion.newObject()
                                                               .name("QueryType")
                                                               /** Static Scalars */
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("bigInteger")
                                                                               .type(Scalars.INSTANCE.getGraphQLBigInteger())
                                                                               .staticValue(BigInteger.valueOf(9999)))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("bigDecimal")
                                                                               .type(Scalars.INSTANCE.getGraphQLBigDecimal())
                                                                               .staticValue(BigDecimal.valueOf(1234.0)))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("floatNaN")
                                                                               .type(Scalars.INSTANCE.getGraphQLFloat())
                                                                               .staticValue(Double.NaN))




                                                               /** Scalars with input of same type, value echoed back */
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("bigIntegerInput")
                                                                               .type(Scalars.INSTANCE.getGraphQLBigInteger())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(new GraphQLNonNull(Scalars.INSTANCE.getGraphQLBigInteger())))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("bigDecimalInput")
                                                                               .type(Scalars.INSTANCE.getGraphQLBigDecimal())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(new GraphQLNonNull(Scalars.INSTANCE.getGraphQLBigDecimal())))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("floatNaNInput")
                                                                               .type(Scalars.INSTANCE.getGraphQLFloat())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(new GraphQLNonNull(Scalars.INSTANCE.getGraphQLFloat())))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("stringInput")
                                                                               .type(Scalars.INSTANCE.getGraphQLString())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(new GraphQLNonNull(Scalars.INSTANCE.getGraphQLString())))
                                                                               .dataFetcher(inputDF))






                                                               /** Scalars with input of String, cast to scalar */
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("bigIntegerString")
                                                                               .type(Scalars.INSTANCE.getGraphQLBigInteger())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("bigDecimalString")
                                                                               .type(Scalars.INSTANCE.getGraphQLBigDecimal())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("floatString")
                                                                               .type(Scalars.INSTANCE.getGraphQLFloat())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("longString")
                                                                               .type(Scalars.INSTANCE.getGraphQLLong())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("intString")
                                                                               .type(Scalars.INSTANCE.getGraphQLInt())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("shortString")
                                                                               .type(Scalars.INSTANCE.getGraphQLShort())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .field(Companion.newFieldDefinition()
                                                                               .name("byteString")
                                                                               .type(Scalars.INSTANCE.getGraphQLByte())
                                                                               .argument(Companion.newArgument()
                                       .name("input")
                                       .type(Scalars.INSTANCE.getGraphQLString()))
                                                                               .dataFetcher(inputDF))
                                                               .build();


    public static final GraphQLSchema scalarsQuerySchema = GraphQLSchema.Companion.newSchema()
                                                                                  .query(queryType)
                                                                                  .build();
}
