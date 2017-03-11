package graphql.schema


import graphql.Assert

import java.util.*

import graphql.Assert.assertNotNull
import graphql.IncludeDirective
import graphql.SkipDirective
import graphql.schema.validation.InvalidSchemaException
import graphql.schema.validation.Validator
import kotlin.properties.Delegates

class GraphQLSchema(val queryType: GraphQLObjectType,
                    val mutationType: GraphQLObjectType? = null,
                    val dictionary: Set<GraphQLType> = emptySet<GraphQLType>()) {
    private val _typeMap: Map<String, GraphQLType>

    init {
        assertNotNull(dictionary, "dictionary can't be null")
        assertNotNull(queryType, "queryType can't be null")
        _typeMap = SchemaUtil().allTypes(this, dictionary)
    }

    fun type(typeName: String): GraphQLType? {
        return _typeMap[typeName]
    }

    val allTypesAsList: List<GraphQLType>
        get() = ArrayList(_typeMap.values)

    val directives: List<GraphQLDirective>
        get() = listOf(IncludeDirective, SkipDirective)

    fun directive(name: String): GraphQLDirective? {
        return directives.firstOrNull { it.name == name }
    }

    val isSupportingMutations: Boolean
        get() = mutationType != null

    class Builder {
        var query: GraphQLObjectType by Delegates.notNull<GraphQLObjectType>()
        var mutation: GraphQLObjectType? = null

        fun query(builder: GraphQLObjectType.Builder): Builder {
            return query(builder.build())
        }

        fun query(queryType: GraphQLObjectType): Builder {
            this.query = queryType
            return this
        }


        fun mutation(builder: GraphQLObjectType.Builder): Builder {
            return mutation(builder.build())
        }

        fun mutation(mutationType: GraphQLObjectType): Builder {
            this.mutation = mutationType
            return this
        }

        fun build(dictionary: Set<GraphQLType> = emptySet<GraphQLType>()): GraphQLSchema {
            Assert.assertNotNull(dictionary, "dictionary can't be null")
            val graphQLSchema = GraphQLSchema(query, mutation, dictionary)
            SchemaUtil().replaceTypeReferences(graphQLSchema)
            val errors = Validator().validateSchema(graphQLSchema)
            if (errors.isNotEmpty()) {
                throw InvalidSchemaException(errors)
            }
            return graphQLSchema
        }
    }

    companion object {
        @JvmStatic
        fun newSchema(): Builder {
            return Builder()
        }

        fun newSchema(block: Builder.() -> Unit) : GraphQLSchema {
            val builder = Builder()
            block(builder)
            return builder.build()
        }
    }

}
