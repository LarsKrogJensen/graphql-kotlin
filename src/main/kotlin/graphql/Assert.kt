package graphql

object Assert {

    fun assertNotNull(obj: Any?, errorMessage: String) {
        if (obj != null) return
        throw AssertException(errorMessage)
    }

    fun assertNotEmpty(c: Collection<*>?, errorMessage: String) {
        if (c == null || c.isEmpty()) throw AssertException(errorMessage)
        return
    }

}
