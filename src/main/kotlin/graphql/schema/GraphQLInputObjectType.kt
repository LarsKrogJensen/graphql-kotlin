package graphql.schema

import graphql.Assert.assertNotNull
import graphql.Assert
import graphql.AssertException
import java.util.*
import kotlin.properties.Delegates.notNull

open class GraphQLInputObjectType(override val name: String,
                                  val description: String?,
                                  val fields: List<GraphQLInputObjectField>)
    : GraphQLType, GraphQLInputType, GraphQLUnmodifiedType, GraphQLNullableType {


    private val fieldMap = LinkedHashMap<String, GraphQLInputObjectField>()

    init {
        buildMap(fields)
    }

    private fun buildMap(fields: List<GraphQLInputObjectField>) {
        for (field in fields) {
            val name = field.name
            if (fieldMap.containsKey(name))
                throw AssertException("field $name redefined")
            fieldMap.put(name, field)
        }
    }

    fun field(name: String): GraphQLInputObjectField? {
        return fieldMap[name]
    }

    @GraphQLDslMarker
    class Builder {
        var name: String by notNull<String>()
        var description: String? = null
        val fields = mutableListOf<GraphQLInputObjectField>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun field(block: GraphQLInputObjectField.Builder.()->Unit) {
            fields += newInputField(block)
        }

        fun field(field: GraphQLInputObjectField): Builder {
            assertNotNull(field, "field can't be null")
            fields.add(field)
            return this
        }

        fun field(builder: GraphQLInputObjectField.Builder): Builder {
            this.fields.add(builder.build())
            return this
        }

        fun fields(fields: List<GraphQLInputObjectField>): Builder {
            for (field in fields) {
                field(field)
            }
            return this
        }

        fun build(): GraphQLInputObjectType {
            return GraphQLInputObjectType(name, description, fields)
        }

    }

    class Reference(name: String) :
            GraphQLInputObjectType(name, "", emptyList()), TypeReference

    companion object {

        @JvmStatic
        fun newInputObject(): Builder {
            return Builder()
        }
    }
}

fun newInputObject(block: GraphQLInputObjectType.Builder.() -> Unit): GraphQLInputObjectType {
    val builder = GraphQLInputObjectType.Builder()
    builder.block()
    return builder.build()
}

fun inputRef(name: String): GraphQLInputObjectType.Reference {
    return GraphQLInputObjectType.Reference(name)
}