package graphql.schema;


import java.util.concurrent.CompletionStage;

public interface DataFetcher<T> {

    CompletionStage<T> get(DataFetchingEnvironment environment);
}
