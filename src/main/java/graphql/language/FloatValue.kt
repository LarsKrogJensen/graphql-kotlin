package graphql.language


import java.math.BigDecimal
import java.util.ArrayList

data class FloatValue(var value: BigDecimal) : AbstractNode(), Value {
    override val children: List<Node>
        get() = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false
        val that = node as FloatValue
        return value == that.value
    }
}
