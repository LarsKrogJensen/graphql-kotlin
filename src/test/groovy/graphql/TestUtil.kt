package graphql

import graphql.schema.*

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
