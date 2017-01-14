package graphql.schema;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Fetches data directly from a field.
 */
public class FieldDataFetcher<T> implements DataFetcher<T> {

    /**
     * The name of the field.
     */
    private final String fieldName;

    /**
     * Ctor.
     *
     * @param fieldName The name of the field.
     */
    public FieldDataFetcher(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public CompletionStage<T> get(DataFetchingEnvironment environment) {
        CompletableFuture<T> promise = new CompletableFuture<>();

        Object source = environment.getSource();
        if (source == null) return null;
        if (source instanceof Map) {
            promise.complete((T)((Map<?, ?>) source).get(fieldName));
            return promise;
        }
        promise.complete((T)getFieldValue(source, environment.getFieldType()));
        return promise;
    }

    /**
     * Uses introspection to get the field value.
     *
     * @param object     The object being acted on.
     * @param outputType The output type; ignored in this case.
     * @return An object, or null.
     */
    private T getFieldValue(Object object, GraphQLOutputType outputType) {
        try {
            Field field = object.getClass().getField(fieldName);
            return (T)field.get(object);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
