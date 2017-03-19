package graphql.language

interface Node {

    val children: List<Node>

    val sourceLocation: SourceLocation?

    /**
     * Compares just the content and not the children.

     * @param node the other node to compare to
     * *
     * @return isEqualTo
     */
    fun isEqualTo(node: Node): Boolean
}
