package graphql.execution.batched;

import graphql.schema.DataFetcher;

/**
 * See {@link Batched}.
 */
public interface BatchedDataFetcher<T> extends DataFetcher<T> {
    // Marker interface
}
