package graphql.execution


import graphql.language.ListType
import graphql.language.NonNullType
import graphql.language.Type
import graphql.language.TypeName
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType

object TypeFromAST {


    fun getTypeFromAST(schema: GraphQLSchema, type: Type?): GraphQLType? {
        if (type is ListType) {
            return GraphQLList(getTypeFromAST(schema, type.type)!!)
        } else if (type is NonNullType) {
            return GraphQLNonNull(getTypeFromAST(schema, type.type)!!)
        }

        return schema.type((type as TypeName).name)
    }
}
