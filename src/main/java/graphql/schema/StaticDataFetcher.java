package graphql.schema;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StaticDataFetcher implements DataFetcher {


    private final Object value;

    public StaticDataFetcher(Object value) {
        this.value = value;
    }

    @Override
    public CompletionStage<Object> get(DataFetchingEnvironment environment) {
        return CompletableFuture.completedFuture(value);
    }
}
