package graphql

import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLNonNull

import graphql.introspection.Introspection.DirectiveLocation.*
import graphql.schema.GraphQLArgument.newArgument

val IncludeDirective = GraphQLDirective.newDirective()
        .name("include")
        .description("Directs the executor to include this field or fragment only when the `if` argument is true")
        .argument(newArgument()
                          .name("if")
                          .type(GraphQLNonNull(GraphQLBoolean))
                          .description("Included when true."))
        .validLocations(FRAGMENT_SPREAD, INLINE_FRAGMENT, FIELD)
        .build()

val SkipDirective = GraphQLDirective.newDirective()
        .name("skip")
        .description("Directs the executor to skip this field or fragment when the `if`'argument is true.")
        .argument(newArgument()
                          .name("if")
                          .type(GraphQLNonNull(GraphQLBoolean))
                          .description("Skipped when true."))
        .validLocations(FRAGMENT_SPREAD, INLINE_FRAGMENT, FIELD)
        .build()
