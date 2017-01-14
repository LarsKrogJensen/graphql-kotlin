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

    public static GraphQLObjectType numberHolderType = GraphQLObjectType.newObject()
            .name("NumberHolder")
            .field(newFieldDefinition()
                    .name("theNumber")
                    .type(GraphQLInt))
            .build();

    public static GraphQLObjectType queryType = GraphQLObjectType.newObject()
            .name("queryType")
            .field(newFieldDefinition()
                    .name("numberHolder")
                    .type(numberHolderType))
            .build();

    public static GraphQLObjectType mutationType = GraphQLObjectType.newObject()
            .name("mutationType")
            .field(newFieldDefinition()
                    .name("changeTheNumber")
                    .type(numberHolderType)
                    .argument(newArgument()
                            .name("newNumber")
                            .type(GraphQLInt))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            Integer newNumber = environment.getArgument("newNumber");
                            Root root = (Root) environment.getSource();
                            return CompletableFuture.completedFuture(root.changeNumber(newNumber));
                        }
                    }))
            .field(newFieldDefinition()
                    .name("failToChangeTheNumber")
                    .type(numberHolderType)
                    .argument(newArgument()
                            .name("newNumber")
                            .type(GraphQLInt))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                            Integer newNumber = environment.getArgument("newNumber");
                            Root root = (Root) environment.getSource();
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

    public static GraphQLSchema schema = newSchema()
            .query(queryType)
            .mutation(mutationType)
            .build();

}
