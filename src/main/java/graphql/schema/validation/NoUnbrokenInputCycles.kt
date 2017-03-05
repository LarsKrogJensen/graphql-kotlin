package graphql.schema.validation

import java.util.ArrayList
import java.util.HashSet

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLModifiedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType

/**
 * Schema validation rule ensuring no input type forms an unbroken non-nullable recursion,
 * as such a type would be impossible to satisfy
 */
class NoUnbrokenInputCycles : ValidationRule {

    override fun check(fieldDef: GraphQLFieldDefinition<*>,
                       validationErrorCollector: ValidationErrorCollector) {
        for (argument in fieldDef.arguments) {
            val argumentType = argument.type
            if (argumentType is GraphQLInputObjectType) {
                val path = mutableListOf<String>(argumentType.name)
                check(argumentType, HashSet<GraphQLType>(), path, validationErrorCollector)
            }
        }
    }

    private fun check(type: GraphQLInputObjectType,
                      seen: MutableSet<GraphQLType>,
                      initialPath: MutableList<String>,
                      validationErrorCollector: ValidationErrorCollector) {
        var path = initialPath
        if (seen.contains(type)) {
            validationErrorCollector.addError(ValidationError(ValidationErrorType.UnbrokenInputCycle, errorMessage(path)))
            return
        }
        seen.add(type)

        for (field in type.fields) {
            if (field.type is GraphQLNonNull) {
                val unwrapped = unwrapNonNull(field.type as GraphQLNonNull)
                if (unwrapped is GraphQLInputObjectType) {
                    path = ArrayList(path)
                    path.add(field.name + "!")
                    check(unwrapped, HashSet(seen), path, validationErrorCollector)
                }
            }
        }
    }

    private fun unwrapNonNull(type: GraphQLNonNull): GraphQLType {
        if (type.wrappedType is GraphQLList) {
            //we only care about [type!]! i.e. non-null lists of non-nulls
            if (type.wrappedType.wrappedType is GraphQLNonNull) {
                return unwrap(type.wrappedType.wrappedType)
            } else {
                return type.wrappedType
            }
        } else {
            return unwrap(type.wrappedType)
        }
    }

    private fun unwrap(type: GraphQLType): GraphQLType {
        if (type is GraphQLModifiedType) {
            return unwrap(type.wrappedType)
        }
        return type
    }

    private fun errorMessage(path: List<String>): String {
        val message = StringBuilder()
        message.append("[")
        for (i in path.indices) {
            if (i != 0) {
                message.append(".")
            }
            message.append(path[i])
        }
        message.append("] forms an unsatisfiable cycle")
        return message.toString()
    }
}
