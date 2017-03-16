package graphql.relay


import graphql.schema.DataFetchingEnvironment

import java.util.ArrayList

class SimpleListConnection<T>(data: List<T>) {
    private val data = ArrayList<T>(data)

    private fun buildEdges(): List<Edge<T>> {
        val edges = ArrayList<Edge<T>>()
        var ix = 0
        for (obj in data) {
            edges.add(Edge(obj, ConnectionCursor(createCursor(ix++))))
        }
        return edges
    }


    operator fun get(environment: DataFetchingEnvironment): Connection<T> {
        var edges = buildEdges()


        val afterOffset = getOffsetFromCursor(environment.argument<String>("after"), -1)
        val begin = Math.max(afterOffset, -1) + 1
        val beforeOffset = getOffsetFromCursor(environment.argument<String>("before"), edges.size)
        val end = Math.min(beforeOffset, edges.size)

        edges = edges.subList(begin, end)
        if (edges.isEmpty()) {
            return emptyConnection()
        }


        val first = environment.argument<Int>("first") ?: 0
        val last = environment.argument<Int>("last") ?: edges.size

        val firstPresliceCursor = edges[0].cursor
        val lastPresliceCursor = edges[edges.size - 1].cursor


        edges = edges.subList(0, if (first <= edges.size) first else edges.size)
        edges = edges.subList(edges.size - last, edges.size)

        if (edges.isEmpty()) {
            return emptyConnection()
        }

        val firstEdge = edges[0]
        val lastEdge = edges[edges.size - 1]

        val pageInfo = PageInfo().apply {
            startCursor = firstEdge.cursor
            endCursor = lastEdge.cursor
            isHasPreviousPage = firstEdge.cursor != firstPresliceCursor
            isHasNextPage = lastEdge.cursor != lastPresliceCursor
        }

        val connection = Connection<T>()
        connection.edges = edges
        connection.pageInfo = pageInfo

        return connection
    }

    private fun emptyConnection(): Connection<T> {
        val connection = Connection<T>()
        connection.pageInfo = PageInfo()
        return connection
    }


    fun cursorForObjectInConnection(obj: T): ConnectionCursor {
        val index = data.indexOf(obj)
        val cursor = createCursor(index)
        return ConnectionCursor(cursor)
    }


    private fun getOffsetFromCursor(cursor: String?, defaultValue: Int): Int {
        if (cursor == null) return defaultValue
        val string = Base64.fromBase64(cursor)
        return Integer.parseInt(string.substring(DUMMY_CURSOR_PREFIX.length))
    }

    private fun createCursor(offset: Int): String {
        return Base64.toBase64(DUMMY_CURSOR_PREFIX + Integer.toString(offset))
    }

    companion object {
        private val DUMMY_CURSOR_PREFIX = "simple-cursor"
    }
}