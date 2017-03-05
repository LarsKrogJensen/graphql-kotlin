package graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;

public class NestedInputSchema {


    public static GraphQLSchema createSchema() {


        GraphQLObjectType root = rootType();

        return GraphQLSchema.Companion.newSchema()
                                      .query(root)
                                      .build();
    }

    public static GraphQLObjectType rootType() {
        return GraphQLObjectType.Companion.newObject()

                                          .name("Root")
                                          .field(GraphQLFieldDefinition.Companion.newFieldDefinition()
                                                                                 .name("value")
                                                                                 .type(INSTANCE.getGraphQLInt())
                                                                                 .dataFetcher(new DataFetcher() {
                            @Override
                            public CompletionStage<Object> fetch(DataFetchingEnvironment environment) {
                                Integer initialValue = environment.argument("initialValue");
                                Map<String, Object> filter = environment.argument("filter");
                                if (filter != null) {
                                    if (filter.containsKey("even")) {
                                        Boolean even = (Boolean) filter.get("even");
                                        if (even && (initialValue%2 != 0)) {
                                            return CompletableFuture.completedFuture(0);
                                        } else if (!even && (initialValue%2 == 0)) {
                                            return CompletableFuture.completedFuture(0);
                                        }
                                    }
                                    if (filter.containsKey("range")) {
                                        Map<String, Integer> range = (Map<String, Integer>) filter.get("range");
                                        if (initialValue < range.get("lowerBound") ||
                                                initialValue > range.get("upperBound")) {
                                            return CompletableFuture.completedFuture(0);
                                        }
                                    }
                                }
                                return CompletableFuture.completedFuture(initialValue);
                            }})
                                                                                 .argument(GraphQLArgument.Companion.newArgument()
                                                           .name("intialValue")
                                                           .type(INSTANCE.getGraphQLInt())
                                                           .defaultValue(5))
                                                                                 .argument(GraphQLArgument.Companion.newArgument()
                                                           .name("filter")
                                                           .type(filterType())))
                                          .build();
    }

    public static GraphQLInputObjectType filterType() {
        return GraphQLInputObjectType.Companion.newInputObject()
                                               .name("Filter")
                                               .field(GraphQLInputObjectField.Companion.newInputObjectField()
                                                        .name("even")
                                                        .type(INSTANCE.getGraphQLBoolean()))
                                               .field(GraphQLInputObjectField.Companion.newInputObjectField()
                                                        .name("range")
                                                        .type(rangeType()))
                                               .build();
    }

    public static GraphQLInputObjectType rangeType() {
        return GraphQLInputObjectType.Companion.newInputObject()
                                               .name("Range")
                                               .field(GraphQLInputObjectField.Companion.newInputObjectField()
                                                        .name("lowerBound")
                                                        .type(INSTANCE.getGraphQLInt()))
                                               .field(GraphQLInputObjectField.Companion.newInputObjectField()
                                                        .name("upperBound")
                                                        .type(INSTANCE.getGraphQLInt()))
                                               .build();
    }
}
