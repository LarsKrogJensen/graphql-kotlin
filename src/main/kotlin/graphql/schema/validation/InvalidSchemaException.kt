package graphql.schema.validation

import graphql.GraphQLException

class InvalidSchemaException(errors: Collection<ValidationError>)
    : GraphQLException(buildMessage(errors))

private fun buildMessage(errors: Collection<ValidationError>): String {
    val message = StringBuilder("invalid schema:")
    for ((_, description) in errors) {
        message.append("\n").append(description)
    }
    return message.toString()
}