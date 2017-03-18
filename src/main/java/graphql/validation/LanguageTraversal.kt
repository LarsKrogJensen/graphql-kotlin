package graphql.validation


import graphql.language.Node

class LanguageTraversal() {

    private val _path: MutableList<Node> = mutableListOf()

    constructor(basePath:List<Node>) : this() {
        _path.addAll(basePath)
    }

    fun traverse(root: Node, queryLanguageVisitor: QueryLanguageVisitor) {
        traverseImpl(root, queryLanguageVisitor, _path)
    }

    private fun traverseImpl(root: Node,
                             queryLanguageVisitor: QueryLanguageVisitor,
                             path: MutableList<Node>) {
        queryLanguageVisitor.enter(root, path)
        path.add(root)
        for (child in root.children) {
            traverseImpl(child, queryLanguageVisitor, path)
        }

        path.removeAt(path.size-1)
        queryLanguageVisitor.leave(root, path)
    }
}
