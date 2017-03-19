package graphql.execution

import graphql.IncludeDirective
import graphql.SkipDirective
import graphql.language.Directive


class ConditionalNodes {

    private var valuesResolver: ValuesResolver = ValuesResolver()

    fun shouldInclude(executionContext: ExecutionContext, directives: List<Directive>): Boolean {

        val skipDirective = findDirective(directives, SkipDirective.name)
        if (skipDirective != null) {
            val argumentValues = valuesResolver.argumentValues(SkipDirective.arguments,
                                                               skipDirective.arguments,
                                                               executionContext.variables)
            return !(argumentValues["if"] as Boolean)
        }


        val includeDirective = findDirective(directives, IncludeDirective.name)
        if (includeDirective != null) {
            val argumentValues = valuesResolver.argumentValues(IncludeDirective.arguments,
                                                               includeDirective.arguments,
                                                               executionContext.variables)
            return argumentValues["if"] as Boolean
        }

        return true
    }

    private fun findDirective(directives: List<Directive>, name: String): Directive? {
        return directives.firstOrNull { it.name == name }
    }

}
