package graphql


import graphql.language.SourceLocation

interface GraphQLError {

    fun message(): String?

    fun locations(): List<SourceLocation>?

    fun errorType(): ErrorType

    /**
     * This little helper allows GraphQlErrors to implement
     * common things (hashcode/ equals) more easily
     */
    object Helper {

        fun hashCode(dis: GraphQLError): Int {
            var result = if (dis.message() != null) dis.message()!!.hashCode() else 0
            result = 31 * result + if (dis.locations() != null) dis.locations()!!.hashCode() else 0
            result = 31 * result + dis.errorType().hashCode()
            return result
        }

        fun equals(dis: GraphQLError, dat: GraphQLError): Boolean {
            if (dis === dat) {
                return true
            }
//            if (dis == null)
//                return false
//            if (dat == null)
//                return false

            if (if (dis.message() != null) dis.message() != dat.message() else dat.message() != null)
                return false
            if (if (dis.locations() != null) dis.locations() != dat.locations() else dat.locations() != null)
                return false
            return dis.errorType() === dat.errorType()
        }
    }

}
