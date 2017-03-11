package graphql.schema


import graphql.Assert.assertNotEmpty
import java.util.ArrayList

import kotlin.properties.Delegates.notNull

class GraphQLUnionType(override val name: String,
                       val description: String?,
                       types: List<GraphQLObjectType>,
                       val typeResolver: TypeResolver)
    : GraphQLType, GraphQLOutputType, GraphQLCompositeType, GraphQLUnmodifiedType, GraphQLNullableType {
    private val types = mutableListOf<GraphQLObjectType>()

    init {
        assertNotEmpty(types, "A Union type must define one or more member types.")
        this.types.addAll(types)
    }

    internal fun replaceTypeReferences(typeMap: Map<String, GraphQLType>) {
        for (i in types.indices) {
            val type = types[i]
            if (type is TypeReference) {
                this.types[i] = SchemaUtil().resolveTypeReference(type, typeMap) as GraphQLObjectType
            }
        }
    }

    fun types(): List<GraphQLObjectType> {
        return types
    }

    class Builder {
        private var name: String by notNull<String>()
        private var description: String? = null
        private val types = ArrayList<GraphQLObjectType>()
        private var typeResolver: TypeResolver by notNull<TypeResolver>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }


        fun typeResolver(typeResolver: TypeResolver): Builder {
            this.typeResolver = typeResolver
            return this
        }


        fun possibleType(type: GraphQLObjectType): Builder {
            types.add(type)
            return this
        }

        fun possibleTypes(vararg type: GraphQLObjectType): Builder {
            for (graphQLType in type) {
                possibleType(graphQLType)
            }
            return this
        }

        fun build(): GraphQLUnionType {
            return GraphQLUnionType(name, description, types, typeResolver)
        }
    }

    companion object {
        @JvmStatic
        fun newUnionType(): Builder {
            return Builder()
        }
    }
}
