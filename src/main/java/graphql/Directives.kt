package graphql

import graphql.introspection.Introspection.DirectiveLocation.*
import graphql.schema.GraphQLNonNull
import graphql.schema.newDirective

val IncludeDirective = newDirective {
    name = "include"
    description = "Directs the executor to include this field or fragment only when the `if` argument is true"
    argument {
        name = "if"
        description = "Include when true."
        type = GraphQLNonNull(GraphQLBoolean)
    }
    locations += FRAGMENT_SPREAD
    locations += INLINE_FRAGMENT
    locations += FIELD
}

val SkipDirective = newDirective {
    name = "skip"
    description = "Directs the executor to skip this field or fragment when the `if`'argument is true."
    argument {
        name = "if"
        description = "Skipped when true."
        type = GraphQLNonNull(GraphQLBoolean)
    }
    locations += FRAGMENT_SPREAD
    locations += INLINE_FRAGMENT
    locations += FIELD
}
