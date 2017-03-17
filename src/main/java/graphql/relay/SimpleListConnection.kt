package graphql.relay


import graphql.schema.DataFetchingEnvironment

private val DEFAULT_CURSOR_PREFIX = "default-cursor"

class SimpleListConnection<T>(private val data: List<T>)  {

    fun fetch(environment: DataFetchingEnvironment): Connection<T> {

        var edges = data.withIndex().map { (index, obj) ->
            DefaultEdge(obj, DefaultConnectionCursor(createCursor(index)))
        }


        val afterOffset = offsetFromCursor(environment.argument<String>("after"), -1)
        val begin = Math.max(afterOffset, -1) + 1
        val beforeOffset = offsetFromCursor(environment.argument<String>("before"), edges.size)
        val end = Math.min(beforeOffset, edges.size)

        edges = edges.subList(begin, end)
        if (edges.isEmpty()) {
            return emptyConnection()
        }
        
        val first = environment.argument<Int>("first")
        val last = environment.argument<Int>("last")

        val firstPresliceCursor = edges[0].cursor
        val lastPresliceCursor = edges[edges.size - 1].cursor

        if (first != null) {
            edges = edges.subList(0, if (first <= edges.size) first else edges.size)
        }
        if (last != null) {
            edges = edges.subList(if (last > edges.size) 0 else edges.size - last, edges.size)
        }

        if (edges.isEmpty()) {
            return emptyConnection()
        }

        val firstEdge = edges[0]
        val lastEdge = edges[edges.size - 1]

        val pageInfo = DefaultPageInfo()
        pageInfo.startCursor = firstEdge.cursor
        pageInfo.endCursor = lastEdge.cursor
        pageInfo.isHasPreviousPage = firstEdge.cursor != firstPresliceCursor
        pageInfo.isHasNextPage = lastEdge.cursor != lastPresliceCursor


        return DefaultConnection(edges, pageInfo)
    }

    private fun emptyConnection() = DefaultConnection<T>(emptyList(), DefaultPageInfo())


    fun cursorForObjectInConnection(obj: T): ConnectionCursor {
        val index = data.indexOf(obj)
        val cursor = createCursor(index)
        return DefaultConnectionCursor(cursor)
    }

    private fun offsetFromCursor(cursor: String?, defaultValue: Int): Int {
        if (cursor == null) return defaultValue
        val string = Base64.fromBase64(cursor)
        return Integer.parseInt(string.substring(DEFAULT_CURSOR_PREFIX.length))
    }

    private fun createCursor(offset: Int): String {
        val string = Base64.toBase64(DEFAULT_CURSOR_PREFIX + Integer.toString(offset))
        return string
    }
}