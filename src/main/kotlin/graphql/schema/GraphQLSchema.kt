package graphql.schema


import graphql.*
import graphql.Assert.assertNotNull
import graphql.schema.validation.InvalidSchemaException
import graphql.schema.validation.Validator
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@DslMarker
annotation class GraphQLDslMarker

class GraphQLSchema(val queryType: GraphQLObjectType,
                    val mutationType: GraphQLObjectType? = null,
                    val subscriptionType: GraphQLObjectType? = null,
                    val addtionalTypes: Set<GraphQLType> = emptySet<GraphQLType>()) {
    private val _typeMap: Map<String, GraphQLType>

    init {
        assertNotNull(addtionalTypes, "addtitionalTypes can't be null")
        assertNotNull(queryType, "queryType can't be null")
        _typeMap = SchemaUtil().allTypes(this, addtionalTypes)
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
        var subscription: GraphQLObjectType? = null
        var additionalTypes: Set<GraphQLType> = emptySet()

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

        fun subscription(builder: GraphQLObjectType.Builder): Builder {
            return subscription(builder.build())
        }

        fun subscription(subscriptionType: GraphQLObjectType): Builder {
            this.subscription = subscriptionType
            return this
        }

        fun build(additionalTypes: Set<GraphQLType> = emptySet<GraphQLType>()): GraphQLSchema {
            assertNotNull(additionalTypes, "additionalTypes can't be null")
            val graphQLSchema = GraphQLSchema(query, mutation, subscription, additionalTypes)
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


    }
}

fun newSchema(block: GraphQLSchema.Builder.() -> Unit): GraphQLSchema {
    val builder = GraphQLSchema.Builder()
    builder.block()
    return builder.build(builder.additionalTypes)
}


fun <T : Any> typeResolve(type: KClass<T>): GraphQLOutputType? = when (type) {
    String::class     -> GraphQLString
    Date::class       -> GraphQLDate
    Int::class        -> GraphQLInt
    Short::class      -> GraphQLShort
    Byte::class       -> GraphQLByte
    Long::class       -> GraphQLLong
    Float::class      -> GraphQLFloat
    Double::class     -> GraphQLFloat
    Boolean::class    -> GraphQLBoolean
    BigInteger::class -> GraphQLBigInteger
    BigDecimal::class -> GraphQLBigDecimal
    else              -> null
}
