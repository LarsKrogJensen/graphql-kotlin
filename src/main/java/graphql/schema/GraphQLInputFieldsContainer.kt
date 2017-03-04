package graphql.schema

interface GraphQLInputFieldsContainer : GraphQLType {

    fun fieldDefinition(name: String): GraphQLInputObjectField

    val fieldDefinitions: List<GraphQLInputObjectField>
}