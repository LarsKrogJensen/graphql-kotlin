package graphql.validation


import graphql.language.Document
import graphql.language.FragmentDefinition
import graphql.schema.*
import java.util.*

interface IValidationContext {
    val schema: GraphQLSchema
    val document: Document
    val traversalContext: ITraversalContext
    val parentType: GraphQLCompositeType?
    val inputType: GraphQLInputType
    val fieldDef: GraphQLFieldDefinition<*>?
    val directive: GraphQLDirective?
    val argument: GraphQLArgument?
    val outputType: GraphQLOutputType?
    fun fragment(name: String): FragmentDefinition?
}

class ValidationContext(override val schema: GraphQLSchema,
                        override val document: Document) : IValidationContext {

    override val traversalContext: TraversalContext = TraversalContext(schema)
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

    override fun fragment(name: String): FragmentDefinition? {
        return _fragmentDefinitionMap[name]
    }

    override val parentType: GraphQLCompositeType?
        get() = traversalContext.parentType

    override val inputType: GraphQLInputType
        get() = traversalContext.inputType

    override val fieldDef: GraphQLFieldDefinition<*>?
        get() = traversalContext.fieldDef

    override val directive: GraphQLDirective?
        get() = traversalContext.directive

    override val argument: GraphQLArgument?
        get() = traversalContext.argument

    override val outputType: GraphQLOutputType?
        get() = traversalContext.outputType

}
