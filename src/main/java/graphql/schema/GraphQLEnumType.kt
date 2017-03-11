package graphql.schema


import graphql.language.EnumValue
import graphql.AssertException

import java.util.ArrayList
import java.util.LinkedHashMap

import graphql.Assert.assertNotNull
import kotlin.properties.Delegates

class GraphQLEnumType(override val name: String,
                      val description: String?,
                      values: List<GraphQLEnumValueDefinition>)
    : GraphQLType, GraphQLInputType, GraphQLOutputType, GraphQLUnmodifiedType {

    private val valueDefinitionMap = LinkedHashMap<String, GraphQLEnumValueDefinition>()

    val coercing: Coercing<*, *> = object : Coercing<Any?, Any?> {
        override fun serialize(input: Any?): Any? {
            return nameByValue(input)
        }

        override fun parseValue(input: Any?): Any? {
            return valueByName(input)
        }

        override fun parseLiteral(input: Any?): Any? {
            if (input !is EnumValue) return null
            val enumValueDefinition = valueDefinitionMap[input.name] ?: return null
            return enumValueDefinition.value
        }
    }

    private fun valueByName(value: Any?): Any? {
        val enumValueDefinition = valueDefinitionMap[value]
        if (enumValueDefinition != null) return enumValueDefinition.value
        return null
    }

    private fun nameByValue(value: Any?): Any? {
        if (value == null) {
            for (valueDefinition in valueDefinitionMap.values) {
                if (valueDefinition.value == null) return valueDefinition.name
            }
        } else {
            for (valueDefinition in valueDefinitionMap.values) {
                if (value == valueDefinition.value) return valueDefinition.name
            }
        }
        return null
    }

    val values: List<GraphQLEnumValueDefinition>
        get() = ArrayList(valueDefinitionMap.values)


    init {
        assertNotNull(name, "name can't be null")
        buildMap(values)
    }

    private fun buildMap(values: List<GraphQLEnumValueDefinition>) {
        for (valueDefinition in values) {
            val name = valueDefinition.name
            if (valueDefinitionMap.containsKey(name))
                throw AssertException("value $name redefined")
            valueDefinitionMap.put(name, valueDefinition)
        }
    }

    class Builder {
        private var name: String by Delegates.notNull<String>()
        private var description: String? = null
        private val values = ArrayList<GraphQLEnumValueDefinition>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun value(name: String, value: Any, description: String, deprecationReason: String): Builder {
            values.add(GraphQLEnumValueDefinition(name, description, value, deprecationReason))
            return this
        }

        fun value(name: String, value: Any, description: String): Builder {
            values.add(GraphQLEnumValueDefinition(name, description, value))
            return this
        }

        fun value(name: String, value: Any): Builder {
            values.add(GraphQLEnumValueDefinition(name, null, value))
            return this
        }

        fun value(name: String): Builder {
            values.add(GraphQLEnumValueDefinition(name, null, name))
            return this
        }

        fun build(): GraphQLEnumType {
            return GraphQLEnumType(name, description, values)
        }
    }

    companion object {
        @JvmStatic
        fun newEnum(): Builder {
            return Builder()
        }
    }
}
