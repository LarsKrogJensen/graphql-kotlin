package graphql.schema


/**
 * A special type to allow a object/interface types to reference itself. It's replaced with the real type
 * object when the schema is build.
 */
class GraphQLTypeReference(override val name: String) : GraphQLType, GraphQLOutputType, TypeReference
