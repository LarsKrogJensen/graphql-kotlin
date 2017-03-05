package graphql.validation


import graphql.language.Node

interface QueryLanguageVisitor {

    fun enter(node: Node, path: List<Node>)

    fun leave(node: Node, path: List<Node>)
}
