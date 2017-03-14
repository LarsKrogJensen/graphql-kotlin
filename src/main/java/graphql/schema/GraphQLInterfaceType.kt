package graphql.schema

import graphql.Assert.assertNotNull
import graphql.AssertException
import java.util.*
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus
import kotlin.properties.Delegates.notNull

open class GraphQLInterfaceType(override val name: String,
                                val description: String?,
                                fieldDefinitions: List<GraphQLFieldDefinition<*>>,
                                val typeResolver: TypeResolver)
    : GraphQLType,
        GraphQLOutputType,
        GraphQLFieldsContainer,
        GraphQLCompositeType,
        GraphQLUnmodifiedType,
        GraphQLNullableType {

    private val fieldDefinitionsByName = linkedMapOf<String, GraphQLFieldDefinition<*>>()

    init {
        buildDefinitionMap(fieldDefinitions)
    }

    private fun buildDefinitionMap(fieldDefinitions: List<GraphQLFieldDefinition<*>>) {
        for (fieldDefinition in fieldDefinitions) {
            val name = fieldDefinition.name
            if (fieldDefinitionsByName.containsKey(name))
                throw AssertException("field $name redefined")
            fieldDefinitionsByName.put(name, fieldDefinition)
        }
    }

    override val fieldDefinitions: List<GraphQLFieldDefinition<*>>
        get() = ArrayList(fieldDefinitionsByName.values)

    override fun toString(): String {
        return "GraphQLInterfaceType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fieldDefinitionsByName=" + fieldDefinitionsByName +
                ", typeResolver=" + typeResolver +
                '}'
    }


    @GraphQLDslMarker
    class Builder {
        var name: String by notNull<String>()
        var description: String? = null
        val fields = ArrayList<GraphQLFieldDefinition<*>>()
        var typeResolver: TypeResolver by notNull<TypeResolver>()


        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        inline fun <reified T : Any> field(block: GraphQLFieldDefinition.Builder<T>.() -> Unit) {
            this.fields += newField<T>(block)
        }

        fun field(fieldDefinition: GraphQLFieldDefinition<*>): Builder {
            fields.add(fieldDefinition)
            return this
        }

        /**
         * Same effect as the field(GraphQLFieldDefinition). Builder.build() is called
         * from within

         * @param builder an un-built/incomplete GraphQLFieldDefinition
         * *
         * @return this
         */
        fun field(builder: GraphQLFieldDefinition.Builder<*>): Builder {
            this.fields.add(builder.build())
            return this
        }

        fun fields(fieldDefinitions: List<GraphQLFieldDefinition<*>>): Builder {
            assertNotNull(fieldDefinitions, "fieldDefinitions can't be null")
            fields.addAll(fieldDefinitions)
            return this
        }

        fun typeResolver(typeResolver: TypeResolver): Builder {
            this.typeResolver = typeResolver
            return this
        }

        fun build(): GraphQLInterfaceType {
            return GraphQLInterfaceType(name, description, fields, typeResolver)
        }
    }

    class Reference(name: String) : GraphQLInterfaceType(name, "", emptyList<GraphQLFieldDefinition<*>>(), typeResolverProxy()), TypeReference


    companion object {

        @JvmStatic
        fun newInterface(): Builder {
            return Builder()
        }

        fun reference(name: String): Reference {
            return Reference(name)
        }
    }
}

fun newInterface(block: GraphQLInterfaceType.Builder.() -> Unit): GraphQLInterfaceType {
    val builder = GraphQLInterfaceType.Builder()
    builder.block()
    return builder.build()
}

