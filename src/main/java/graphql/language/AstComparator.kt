package graphql.language

class AstComparator {
  fun isEqual(node1: Node, node2: Node): Boolean {
        if (!node1.isEqualTo(node2)) return false
        val childs1 = node1.children
        val childs2 = node2.children
        if (childs1.size != childs2.size) return false
        for (i in childs1.indices) {
            if (!isEqual(childs1[i], childs2[i])) return false
        }
        return true
    }
}
