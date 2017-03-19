package graphql

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLSchema
import graphql.schema.newObject
import graphql.schema.newSchema

fun schemaWithInputType(inputType: GraphQLInputType) : GraphQLSchema {
      return newSchema {
          query = newObject {
              name = "query"
              field<String> {
                  name = "name"
                  argument {
                      name = "arg"
                      type = inputType
                  }
              }
          }
      }
    }

val dummySchema = newSchema {
    query = newObject {
        name = "QueryType"
    }
}
