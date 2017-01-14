package graphql;


import graphql.schema.*;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.*;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.junit.Assert.assertEquals;

public class HelloWorld
{
    public static CompletionStage<String> sayHello()
    {
        return CompletableFuture.completedFuture("world");
    }

    public static void main(String[] args)
        throws ExecutionException, InterruptedException
    {
        GraphQLObjectType queryType = newObject()
            .name("helloWorldQuery")
            //.field(new GraphQLFieldDefinition.Builder<String>()
            //           .type(GraphQLString)
            //           .name("hello")
            //           .dataFetcher(env -> sayHello()))

            .field(GraphQLFieldDefinition.<String>newFieldDefinition()
                       .type(GraphQLString)
                       .name("hello")
                       .dataFetcher((env) -> sayHello()))
            //.staticValue("world"))
            .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(queryType)
                                            .build();
        Map<String, Object> result = (Map<String, Object>)((CompletableFuture<ExecutionResult>)new GraphQL(schema).execute("{hello}")).get()
                                                                                                                                      .getData();
        System.out.println(result);
    }

    @Test
    public void helloWorldTest()
        throws ExecutionException, InterruptedException
    {
        GraphQLObjectType queryType = newObject()
            .name("helloWorldQuery")
            .field(newFieldDefinition()
                       .type(GraphQLString)
                       .name("hello")
                       .staticValue("world"))
            .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(queryType)
                                            .build();
        Map<String, Object> result = (Map<String, Object>)((CompletableFuture<ExecutionResult>)new GraphQL(schema).execute("{hello}")).get()
                                                                                                                                      .getData();
        assertEquals("world", result.get("hello"));
    }
}
