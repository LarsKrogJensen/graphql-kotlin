package graphql.language

// This should probably be an enum... but the grammar
// doesn't enforce the names. These are the current names:
//    QUERY
//    MUTATION
//    FIELD
//    FRAGMENT_DEFINITION
//    FRAGMENT_SPREAD
//    INLINE_FRAGMENT
class DirectiveLocation(private val name: String) : AbstractNode() {


    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as DirectiveLocation

        if (name != that.name) {
            return false
        }
        return true
    }


    override fun toString(): String {
        return "DirectiveLocation{" +
                "name='" + name + "'" +
                "}"
    }
}
