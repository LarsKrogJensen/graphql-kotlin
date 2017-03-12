package graphql.validation

import graphql.schema.*

interface ITraversalContext : QueryLanguageVisitor {
    var directive: GraphQLDirective?
    var argument: GraphQLArgument?
    val outputType: GraphQLOutputType?
    val parentType: GraphQLCompositeType?
    val fieldDef: GraphQLFieldDefinition<*>?
}