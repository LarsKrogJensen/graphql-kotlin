package graphql


import graphql.language.SourceLocation

class ExceptionWhileDataFetching(val exception: Throwable) : GraphQLError {


    override fun message(): String {
        return "Exception while fetching data: " + exception.toString()
    }

    override fun locations(): List<SourceLocation>? {
        return null
    }

    override fun errorType(): ErrorType {
        return ErrorType.DataFetchingException
    }

    override fun toString(): String {
        return "ExceptionWhileDataFetching{" +
                "exception=" + exception +
                '}'
    }
}
