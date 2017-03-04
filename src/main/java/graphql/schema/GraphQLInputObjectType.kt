package graphql.schema

import graphql.Assert.assertNotNull
import graphql.AssertException
import java.util.*
import kotlin.properties.Delegates.notNull

open class GraphQLInputObjectType(override val name: String,
                                  val description: String?,
                                  val fields: List<GraphQLInputObjectField>) : GraphQLType, GraphQLInputType, GraphQLUnmodifiedType, GraphQLNullableType {


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

    class Builder {
        private var name: String by notNull<String>()
        private var description: String? = null
        private val fields = mutableListOf<GraphQLInputObjectField>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun field(field: GraphQLInputObjectField): Builder {
            assertNotNull(field, "field can't be null")
            fields.add(field)
            return this
        }

        /**
         * Take a field builder in a function definition and apply. Can be used in a jdk8 lambda
         * e.g.:
         * <pre>
         * `field(f -> f.name("fieldName"))
        ` *
        </pre> *

         * @param builderFunction a supplier for the builder impl
         * *
         * @return this
         */
        fun field(builderFunction: BuilderFunction<GraphQLInputObjectField.Builder>): Builder {
            assertNotNull(builderFunction, "builderFunction should not be null")
            var builder: GraphQLInputObjectField.Builder = GraphQLInputObjectField.newInputObjectField()
            builder = builderFunction.apply(builder)
            return field(builder)
        }

        /**
         * Same effect as the field(GraphQLFieldDefinition). Builder.build() is called
         * from within

         * @param builder an un-built/incomplete GraphQLFieldDefinition
         * *
         * @return this
         */
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

    class Reference (name: String) :
            GraphQLInputObjectType(name, "", emptyList()), TypeReference

    companion object {

        fun newInputObject(): Builder {
            return Builder()
        }

        fun reference(name: String): Reference {
            return Reference(name)
        }
    }
}
