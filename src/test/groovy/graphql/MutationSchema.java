package graphql;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLSchema.newSchema;

public class MutationSchema {

    public static class NumberHolder {
        int theNumber;

        public NumberHolder(int theNumber) {
            this.theNumber = theNumber;
        }

        public int getTheNumber() {
            return theNumber;
        }

        public void setTheNumber(int theNumber) {
            this.theNumber = theNumber;
        }


    }

    public static class Root {
        NumberHolder numberHolder;

        public Root(int number) {
            this.numberHolder = new NumberHolder(number);
        }

        public NumberHolder changeNumber(int newNumber) {
            this.numberHolder.theNumber = newNumber;
            return this.numberHolder;
        }


        public NumberHolder failToChangeTheNumber(int newNumber) {
            throw new RuntimeException("Cannot change the number");
        }


    }

    public static GraphQLObjectType numberHolderType = GraphQLObjectType.Companion.newObject()
                                                                                  .name("NumberHolder")
                                                                                  .field(Companion.newFieldDefinition()
                                                                                                  .name("theNumber")
                                                                                                  .type(INSTANCE.getGraphQLInt()))
                                                                                  .build();

    public static GraphQLObjectType queryType = GraphQLObjectType.Companion.newObject()
                                                                           .name("queryType")
                                                                           .field(Companion.newFieldDefinition()
                                                                                           .name("numberHolder")
                                                                                           .type(numberHolderType))
                                                                           .build();

    public static GraphQLObjectType mutationType = GraphQLObjectType.Companion.newObject()
                                                                              .name("mutationType")
                                                                              .field(Companion.newFieldDefinition()
                                                                                              .name("changeTheNumber")
                                                                                              .type(numberHolderType)
                                                                                              .argument(Companion.newArgument()
                                       .name("newNumber")
                                       .type(INSTANCE.getGraphQLInt()))
                                                                                              .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> fetch(DataFetchingEnvironment environment) {
                            Integer newNumber = environment.argument("newNumber");
                            Root root = (Root) environment.source();
                            return CompletableFuture.completedFuture(root.changeNumber(newNumber));
                        }
                    }))
                                                                              .field(Companion.newFieldDefinition()
                                                                                              .name("failToChangeTheNumber")
                                                                                              .type(numberHolderType)
                                                                                              .argument(Companion.newArgument()
                                       .name("newNumber")
                                       .type(INSTANCE.getGraphQLInt()))
                                                                                              .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> fetch(DataFetchingEnvironment environment) {
                            Integer newNumber = environment.argument("newNumber");
                            Root root = (Root) environment.source();
                            CompletableFuture<Object> promise = new CompletableFuture<>();
                            try {
                                promise.complete(root.failToChangeTheNumber(newNumber));
                            } catch (Exception e) {
                                promise.completeExceptionally(e);
                            }
                            return promise;
                        }
                    }))
                                                                              .build();

    public static GraphQLSchema schema = Companion.newSchema()
                                                  .query(queryType)
                                                  .mutation(mutationType)
                                                  .build();

}
