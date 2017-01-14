package graphql.relay;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public class SimpleListConnection<T> implements DataFetcher {

    private static final String DUMMY_CURSOR_PREFIX = "simple-cursor";
    private List<T> data = new ArrayList<>();


    public SimpleListConnection(List<T> data) {
        this.data = data;

    }

    private List<Edge<T>> buildEdges() {
        List<Edge<T>> edges = new ArrayList<>();
        int ix = 0;
        for (T object : data) {
            edges.add(new Edge<T>(object, new ConnectionCursor(createCursor(ix++))));
        }
        return edges;
    }


    @Override
    public CompletionStage<Connection<T>> get(DataFetchingEnvironment environment) {

        CompletableFuture<Connection<T>> promise = new CompletableFuture<>();
        List<Edge<T>> edges = buildEdges();


        int afterOffset = getOffsetFromCursor(environment.<String>getArgument("after"), -1);
        int begin = Math.max(afterOffset, -1) + 1;
        int beforeOffset = getOffsetFromCursor(environment.<String>getArgument("before"), edges.size());
        int end = Math.min(beforeOffset, edges.size());

        edges = edges.subList(begin, end);
        if (edges.size() == 0) {
            promise.complete(emptyConnection());
            return promise;
        }


        Integer first = environment.<Integer>getArgument("first");
        Integer last = environment.<Integer>getArgument("last");

        ConnectionCursor firstPresliceCursor = edges.get(0).cursor;
        ConnectionCursor lastPresliceCursor = edges.get(edges.size() - 1).cursor;

        if (first != null) {
            edges = edges.subList(0, first <= edges.size() ? first : edges.size());
        }
        if (last != null) {
            edges = edges.subList(edges.size() - last, edges.size());
        }

        if (edges.size() == 0) {
            promise.complete(emptyConnection());
            return promise;
        }

        Edge<T> firstEdge = edges.get(0);
        Edge<T> lastEdge = edges.get(edges.size() - 1);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setStartCursor(firstEdge.getCursor());
        pageInfo.setEndCursor(lastEdge.getCursor());
        pageInfo.setHasPreviousPage(!firstEdge.getCursor().equals(firstPresliceCursor));
        pageInfo.setHasNextPage(!lastEdge.getCursor().equals(lastPresliceCursor));

        Connection<T> connection = new Connection<>();
        connection.setEdges(edges);
        connection.setPageInfo(pageInfo);

        promise.complete(connection);
        return promise;
    }

    private Connection<T> emptyConnection() {
        Connection<T> connection = new Connection<>();
        connection.setPageInfo(new PageInfo());
        return connection;
    }


    public ConnectionCursor cursorForObjectInConnection(T object) {
        int index = data.indexOf(object);
        String cursor = createCursor(index);
        return new ConnectionCursor(cursor);
    }


    private int getOffsetFromCursor(String cursor, int defaultValue) {
        if (cursor == null) return defaultValue;
        String string = Base64.fromBase64(cursor);
        return Integer.parseInt(string.substring(DUMMY_CURSOR_PREFIX.length()));
    }

    private String createCursor(int offset) {
        return Base64.toBase64(DUMMY_CURSOR_PREFIX + Integer.toString(offset));
    }


}