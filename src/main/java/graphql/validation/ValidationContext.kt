package graphql.validation


import graphql.language.Document
import graphql.language.FragmentDefinition
import graphql.schema.*
import java.util.*

open class ValidationContext(val schema: GraphQLSchema, val document: Document) {

    val traversalContext: TraversalContext = TraversalContext(schema)
    private val _fragmentDefinitionMap = LinkedHashMap<String, FragmentDefinition>()


    init {
        buildFragmentMap()
    }

    private fun buildFragmentMap() {
        for (definition in document.definitions) {
            if (definition !is FragmentDefinition) continue
            val fragmentDefinition = definition
            _fragmentDefinitionMap.put(fragmentDefinition.name, fragmentDefinition)
        }
    }

    fun getFragment(name: String): FragmentDefinition? {
        return _fragmentDefinitionMap[name]
    }

    val parentType: GraphQLCompositeType?
        get() = traversalContext.parentType

    val inputType: GraphQLInputType
        get() = traversalContext.inputType

    val fieldDef: GraphQLFieldDefinition<*>?
        get() = traversalContext.fieldDef

    val directive: GraphQLDirective?
        get() = traversalContext.directive

    val argument: GraphQLArgument?
        get() = traversalContext.argument

    val outputType: GraphQLOutputType?
        get() = traversalContext.outputType

}
