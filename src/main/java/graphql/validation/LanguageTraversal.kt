package graphql.validation


import graphql.language.Node

import java.util.ArrayList

class LanguageTraversal {

    private val path: MutableList<Node>

    constructor() {
        path = ArrayList<Node>()
    }

    constructor(basePath: MutableList<Node>?) {
        if (basePath != null) {
            path = basePath
        } else {
            path = ArrayList<Node>()
        }
    }

    fun traverse(root: Node, queryLanguageVisitor: QueryLanguageVisitor) {
        traverseImpl(root, queryLanguageVisitor, path)
    }

    private fun traverseImpl(root: Node,
                             queryLanguageVisitor: QueryLanguageVisitor,
                             path: MutableList<Node>) {
        queryLanguageVisitor.enter(root, path)
        path.add(root)
        for (child in root.children) {
            traverseImpl(child, queryLanguageVisitor, path)
        }
        path.removeAt(path.size - 1)
        queryLanguageVisitor.leave(root, path)
    }
}
