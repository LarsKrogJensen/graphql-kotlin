package graphql.execution.batched;

import graphql.Scalars;
import graphql.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

public class FunWithStringsSchemaFactory {


    public static void increment(Map<CallType, AtomicInteger> callCounts, CallType type) {
        if (!callCounts.containsKey(type)) {
            callCounts.put(type, new AtomicInteger(0));
        }
        callCounts.get(type).incrementAndGet();
    }

    public enum CallType {
        VALUE, APPEND, WORDS_AND_LETTERS, SPLIT, SHATTER
    }

    public static FunWithStringsSchemaFactory createBatched(final Map<CallType, AtomicInteger> callCounts) {
        FunWithStringsSchemaFactory factory = new FunWithStringsSchemaFactory();


        factory.setStringObjectValueFetcher(new DataFetcher() {
            @Override
            @Batched
            @SuppressWarnings("unchecked")
            public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                increment(callCounts, CallType.VALUE);
                List<String> retVal = new ArrayList<String>();
                for (String s: (List<String>) environment.getSource()) {
                    retVal.add("null".equals(s) ? null : s);
                }
                return CompletableFuture.completedFuture(retVal);
            }
        });

        factory.setAppendFetcher(new DataFetcher() {
            @Override
            @Batched
            @SuppressWarnings("unchecked")
            public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                increment(callCounts, CallType.APPEND);
                List<String> retVal = new ArrayList<String>();
                for (String s: (List<String>) environment.getSource()) {
                    retVal.add(s + environment.getArgument("text"));
                }
                return CompletableFuture.completedFuture(retVal);
            }
        });

        factory.setWordsAndLettersFetcher(new DataFetcher() {
            @Batched
            @Override
            @SuppressWarnings("unchecked")
            public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                increment(callCounts, CallType.WORDS_AND_LETTERS);
                List<String> sources = (List<String>) environment.getSource();
                List<List<List<String>>> retVal = new ArrayList<List<List<String>>>();
                for (String source : sources) {
                    List<List<String>> sentence = new ArrayList<List<String>>();
                    for (String word : source.split(" ")) {
                        List<String> letters = new ArrayList<String>();
                        for (char c : word.toCharArray()) {
                            letters.add(Character.toString(c));
                        }
                        sentence.add(letters);
                    }
                    retVal.add(sentence);
                }
                return CompletableFuture.completedFuture(retVal);
            }
        });

        factory.setSplitFetcher(new DataFetcher() {
            @Batched
            @Override
            @SuppressWarnings("unchecked")
            public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                increment(callCounts, CallType.SPLIT);
                String regex = environment.getArgument("regex");
                List<String> sources = (List<String>) environment.getSource();
                List<List<String>> retVal = new ArrayList<List<String>>();
                if (regex == null) {
                    for (String source: sources) {
                        retVal.add(null);
                    }
                    return CompletableFuture.completedFuture(retVal);
                }
                for (String source: sources) {
                    List<String> retItem = new ArrayList<String>();
                    for (String str: source.split(regex)) {
                        if (str.isEmpty()) {
                            retItem.add(null);
                        } else {
                            retItem.add(str);
                        }
                    }
                    retVal.add(retItem);
                }
                return CompletableFuture.completedFuture(retVal);
            }
        });

        factory.setShatterFetcher(new DataFetcher() {
            @Batched
            @Override
            @SuppressWarnings("unchecked")
            public CompletionStage<Object> get(DataFetchingEnvironment environment) {
                increment(callCounts, CallType.SHATTER);
                List<String> sources = (List<String>) environment.getSource();
                List<List<String>> retVal = new ArrayList<List<String>>();
                for (String source: sources) {
                    List<String> retItem = new ArrayList<String>();
                    for (char c: source.toCharArray()) {
                        retItem.add(Character.toString(c));
                    }
                    retVal.add(retItem);
                }
                return CompletableFuture.completedFuture(retVal);
            }
        });

        return factory;

    }


    private DataFetcher stringObjectValueFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment e) {
            return CompletableFuture.completedFuture("null".equals(e.getSource()) ? null : e.getSource());
        }
    };

    private DataFetcher shatterFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment e) {
            String source = (String) e.getSource();
            if(source.isEmpty()) {
                return null; // trigger error
            }
            List<String> retVal = new ArrayList<String>();
            for (char c: source.toCharArray()) {
                retVal.add(Character.toString(c));
            }
            return CompletableFuture.completedFuture(retVal);
        }
    };

    public DataFetcher wordsAndLettersFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment e) {
            String source = (String) e.getSource();
            List<List<String>> retVal = new ArrayList<List<String>>();
            for (String word: source.split(" ")) {
                List<String> retItem = new ArrayList<String>();
                for (char c: word.toCharArray()) {
                    retItem.add(Character.toString(c));
                }
                retVal.add(retItem);
            }
            return CompletableFuture.completedFuture(retVal);
        }
    };

    public DataFetcher splitFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment e) {
            String regex = e.getArgument("regex");
            if (regex == null ) {
                return null;
            }
            String source = (String) e.getSource();
            List<String> retVal = new ArrayList<String>();
            for (String str: source.split(regex)) {
                if (str.isEmpty()) {
                    retVal.add(null);
                } else {
                    retVal.add(str);
                }
            }
            return CompletableFuture.completedFuture(retVal);
        }
    };

    public DataFetcher appendFetcher = new DataFetcher() {
        @Override
        public CompletionStage<Object> get(DataFetchingEnvironment e) {
            return CompletableFuture.completedFuture(((String)e.getSource()) + e.getArgument("text"));
        }
    };

    public void setWordsAndLettersFetcher(DataFetcher fetcher) {
        this.wordsAndLettersFetcher = fetcher;
    }

    public void setShatterFetcher(DataFetcher fetcher) {
        this.shatterFetcher = fetcher;
    }

    public void setSplitFetcher(DataFetcher splitFetcher) {
        this.splitFetcher = splitFetcher;
    }

    public void setAppendFetcher(DataFetcher appendFetcher) {
        this.appendFetcher = appendFetcher;
    }

    public void setStringObjectValueFetcher(DataFetcher fetcher) {
        this.stringObjectValueFetcher = fetcher;
    }

    GraphQLSchema createSchema() {

        GraphQLObjectType stringObjectType = GraphQLObjectType.newObject()
                .name("StringObject")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("value")
                        .type(Scalars.GraphQLString)
                        .dataFetcher(stringObjectValueFetcher))
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("nonNullValue")
                        .type(new GraphQLNonNull(Scalars.GraphQLString))
                        .dataFetcher(stringObjectValueFetcher))
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("veryNonNullValue")
                        .type(new GraphQLNonNull(new GraphQLNonNull(Scalars.GraphQLString)))
                        .dataFetcher(stringObjectValueFetcher))

                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("shatter")
                        .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference("StringObject")))))
                        .dataFetcher(shatterFetcher))

                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("wordsAndLetters")
                        .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull( new GraphQLNonNull(new GraphQLTypeReference("StringObject"))))))))
                        .dataFetcher(wordsAndLettersFetcher))

                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("split")
                        .description("String#split(regex) but replace empty strings with nulls to help us test null behavior in lists")
                        .type(new GraphQLList(new GraphQLTypeReference("StringObject")))
                        .argument(GraphQLArgument.newArgument()
                                .name("regex")
                                .type(Scalars.GraphQLString))
                        .dataFetcher(splitFetcher))

                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("splitNotNull")
                        .description("String#split(regex) but replace empty strings with nulls to help us test null behavior in lists")
                        .type(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference("StringObject"))))
                        .argument(GraphQLArgument.newArgument()
                                .name("regex")
                                .type(Scalars.GraphQLString))
                        .dataFetcher(splitFetcher))


                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("append")
                        .type(new GraphQLTypeReference("StringObject"))
                        .argument(GraphQLArgument.newArgument()
                                .name("text")
                                .type(Scalars.GraphQLString))
                        .dataFetcher(appendFetcher))

                .build();


        GraphQLEnumType enumDayType = GraphQLEnumType.newEnum()
                .name("Day")
                .value("MONDAY")
                .value("TUESDAY")
                .description("Day of the week")
                .build();

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
                .name("StringQuery")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("string")
                        .type(stringObjectType)
                        .argument(GraphQLArgument.newArgument()
                                .name("value")
                                .type(Scalars.GraphQLString))
                        .dataFetcher(new DataFetcher() {
                            @Override
                            public CompletionStage<Object> get(DataFetchingEnvironment env) {
                                CompletableFuture<Object> promise = new CompletableFuture<>();
                                promise.complete(env.getArgument("value"));
                                return promise;
                            }
                        }))
                .name("EnumQuery")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("nullEnum")
                        .type(enumDayType)
                        .dataFetcher(new DataFetcher() {
                           @Override
                           public CompletionStage<Object> get(DataFetchingEnvironment env) {
                               return CompletableFuture.completedFuture(null);
                           }
                        }))
                .build();
        return GraphQLSchema.newSchema()
                .query(queryType)
                .build();

    }
}
