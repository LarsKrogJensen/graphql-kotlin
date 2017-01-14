package graphql.execution.batched;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Given a normal data fetcher as a delegate,
 * uses that fetcher in a batched context by iterating through each source value and calling
 * the delegate.
 */
public class UnbatchedDataFetcher implements BatchedDataFetcher {

    private final DataFetcher delegate;

    public UnbatchedDataFetcher(DataFetcher delegate) {
        assert !(delegate instanceof BatchedDataFetcher);
        this.delegate = delegate;
    }


    @Override
    public CompletionStage<Object> get(DataFetchingEnvironment environment) {
        @SuppressWarnings("unchecked")
        List<Object> sources = (List<Object>) environment.getSource();
        List<Object> results = new ArrayList<Object>();
        List<CompletableFuture> sourcePromises = new ArrayList<>();
        for (Object source : sources) {
            DataFetchingEnvironment singleEnv = new DataFetchingEnvironment(
                    source,
                    environment.getArguments(),
                    environment.getContext(),
                    environment.getFields(),
                    environment.getFieldType(),
                    environment.getParentType(),
                    environment.getGraphQLSchema());
            if (delegate.get(singleEnv) == null) {
                results.add(null);
                continue;
            }
            sourcePromises.add((CompletableFuture) delegate.get(singleEnv).thenAccept(result -> results.add(result)));
        }

        CompletableFuture[] sourcePromisesArray = new CompletableFuture[sourcePromises.size()];
        return CompletableFuture.allOf(sourcePromises.toArray(sourcePromisesArray)).thenApply(aVoid -> results);
    }
}
