package graphql.schema

import graphql.AssertException

import java.util.*

import graphql.Assert.assertNotNull
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
        val fieldDefinitions = mutableListOf<GraphQLFieldDefinition<*>>()
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
            this.fieldDefinitions.add(fieldDefinition)
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
         * @param <T>             field outputtype
         * *
         * @return this
        </T> */
        fun <T> field(builderFunction: BuilderFunction<GraphQLFieldDefinition.Builder<T>>): Builder {
            assertNotNull(builderFunction, "builderFunction can't be null")
            var builder: GraphQLFieldDefinition.Builder<T> = GraphQLFieldDefinition.newFieldDefinition<T>()
            builder = builderFunction.apply(builder)
            return field(builder.build())
        }

        /**
         * Same effect as the field(GraphQLFieldDefinition). Builder.build() is called
         * from within

         * @param builder an un-built/incomplete GraphQLFieldDefinition
         * *
         * @param <T>     field outputtype
         * *
         * @return this
        </T> */
        fun <T> field(builder: GraphQLFieldDefinition.Builder<T>): Builder {
            this.fieldDefinitions.add(builder.build())
            return this
        }

        inline fun <reified T : Any> field(block: GraphQLFieldDefinition.Builder<T>.() -> Unit) {
            this.fieldDefinitions +=newField<T>(block)
        }

        fun fields(fieldDefinitions: List<GraphQLFieldDefinition<*>>): Builder {
            assertNotNull(fieldDefinitions, "fieldDefinitions can't be null")
            this.fieldDefinitions.addAll(fieldDefinitions)
            return this
        }

        fun withInterface(interfaceType: GraphQLInterfaceType): Builder {
            assertNotNull(interfaceType, "interfaceType can't be null")
            this.interfaces.add(interfaceType)
            return this
        }

        fun withInterfaces(vararg interfaceType: GraphQLInterfaceType): Builder {
            for (type in interfaceType) {
                withInterface(type)
            }
            return this
        }

        fun build(): GraphQLObjectType {
            return GraphQLObjectType(name, description, fieldDefinitions, interfaces)
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

fun newObject(block: GraphQLObjectType.Builder.() -> Unit): GraphQLObjectType {
    val builder = GraphQLObjectType.Builder()
    builder.block()
    return builder.build()
}