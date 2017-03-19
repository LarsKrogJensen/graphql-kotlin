package graphql.language


interface TypeDefinition : Node, Definition {
    /**
     * @return the name of the type being defined.
     */
    val name: String
}
