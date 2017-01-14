package graphql.relay;


public class Edge<T> {

    public Edge(T node, ConnectionCursor cursor) {
        this.node = node;
        this.cursor = cursor;
    }

    T node;
    ConnectionCursor cursor;

    public Object getNode() {
        return node;
    }

    public void setNode(T node) {
        this.node = node;
    }

    public ConnectionCursor getCursor() {
        return cursor;
    }

    public void setCursor(ConnectionCursor cursor) {
        this.cursor = cursor;
    }
}
