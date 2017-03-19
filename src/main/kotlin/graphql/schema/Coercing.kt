package graphql.schema


interface Coercing<out I, out O> {


    /**
     * Called to convert a result of a DataFetcher to a valid runtime value.

     * @param input is never null
     * *
     * @return null if not possible/invalid
     */
    fun serialize(input: Any?): O

    /**
     * Called to resolve a input from a variable.
     * Null if not possible.

     * @param input is never null
     * *
     * @return null if not possible/invalid
     */
    fun parseValue(input: Any?): I

    /**
     * Called to convert a AST node

     * @param input is never null
     * *
     * @return null if not possible/invalid
     */
    fun parseLiteral(input: Any?): I
}
