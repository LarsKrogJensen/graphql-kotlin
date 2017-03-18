package graphql.schema

import graphql.Assert.assertNotNull
import graphql.AssertException
import java.util.*
import kotlin.properties.Delegates.notNull

open class GraphQLObjectType(override val name: String,
                             val description: String?,
                             fieldDefinitions: List<GraphQLFieldDefinition<*>>,
                             interfaces: List<GraphQLInterfaceType>) : GraphQLType, GraphQLOutputType, GraphQLFieldsContainer, GraphQLCompositeType, GraphQLUnmodifiedType, GraphQLNullableType {
    private val _fieldDefinitionsByName = linkedMapOf<String, GraphQLFieldDefinition<*>>()
    private val _interfaces = mutableListOf<GraphQLInterfaceType>()

    init {
        _interfaces.addAll(interfaces)
        buildDefinitionMap(fieldDefinitions)
    }

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        for (i in _interfaces.indices) {
            val inter = _interfaces[i]
            if (inter is TypeReference) {
                this._interfaces[i] = SchemaUtil().resolveTypeReference(inter, typeMap) as GraphQLInterfaceType
            }
        }
    }

    private fun buildDefinitionMap(fieldDefinitions: List<GraphQLFieldDefinition<*>>) {
        for (fieldDefinition in fieldDefinitions) {
            val name = fieldDefinition.name
            if (_fieldDefinitionsByName.containsKey(name))
                throw AssertException("field $name redefined")
            _fieldDefinitionsByName.put(name, fieldDefinition)
        }
    }


    fun fieldDefinition(name: String): GraphQLFieldDefinition<*>? {
        return _fieldDefinitionsByName[name]
    }


    override val fieldDefinitions: List<GraphQLFieldDefinition<*>>
        get() = ArrayList(_fieldDefinitionsByName.values)


    val interfaces: List<GraphQLInterfaceType>
        get() = _interfaces


    override fun toString(): String {
        return "GraphQLObjectType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fieldDefinitionsByName=" + _fieldDefinitionsByName +
                ", interfaces=" + _interfaces +
                '}'
    }

    @GraphQLDslMarker
    class Builder {
        var name: String by notNull<String>()
        var description: String? = null
        val fields = mutableListOf<GraphQLFieldDefinition<*>>()
        val interfaces = mutableListOf<GraphQLInterfaceType>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun <T> field(fieldDefinition: GraphQLFieldDefinition<T>): Builder {
            assertNotNull(fieldDefinition, "fieldDefinition can't be null")
            this.fields.add(fieldDefinition)
            return this
        }

        fun <T> field(builder: GraphQLFieldDefinition.Builder<T>): Builder {
            this.fields.add(builder.build())
            return this
        }

        inline fun <reified TOut : Any> field(block: GraphQLFieldDefinition.Builder<TOut>.() -> Unit) {
            this.fields +=newField(block)
        }

        fun build(): GraphQLObjectType {
            return GraphQLObjectType(name, description, fields, interfaces)
        }
    }

    class Reference constructor(name: String) :
            GraphQLObjectType(name,
                              "",
                              emptyList<GraphQLFieldDefinition<*>>(),
                              emptyList<GraphQLInterfaceType>()), TypeReference


    companion object {

        @JvmStatic
        fun newObject(): Builder {
            return Builder()
        }

        fun reference(name: String): Reference {
            return Reference(name)
        }
    }
}

fun objectRef(name: String) = GraphQLObjectType.Reference(name)

fun newObject(block: GraphQLObjectType.Builder.() -> Unit): GraphQLObjectType {
    val builder = GraphQLObjectType.Builder()
    builder.block()
    return builder.build()
}