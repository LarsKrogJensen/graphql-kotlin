package graphql.relay


import java.util.ArrayList

class Connection<T> {
    var edges: List<Edge<T>> = ArrayList()

    var pageInfo: PageInfo? = null
}
