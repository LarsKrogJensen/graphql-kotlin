package graphql.validation


import graphql.language.Node

import java.util.ArrayList

class LanguageTraversal {

    private val _path: List<Node>

    constructor() {
        _path = listOf<Node>()
    }

    constructor(basePath:List<Node>) {
        _path = basePath
    }

    fun traverse(root: Node, queryLanguageVisitor: QueryLanguageVisitor) {
        traverseImpl(root, queryLanguageVisitor, _path)
    }

    private fun traverseImpl(root: Node,
                             queryLanguageVisitor: QueryLanguageVisitor,
                             path: List<Node>) {
        queryLanguageVisitor.enter(root, path)
        for (child in root.children.asSequence().plus(root)) {
            traverseImpl(child, queryLanguageVisitor, path)
        }
        queryLanguageVisitor.leave(root, path)
    }
}
