package graphql.language


abstract class AbstractNode : Node {

    override var sourceLocation: SourceLocation? = null
}
