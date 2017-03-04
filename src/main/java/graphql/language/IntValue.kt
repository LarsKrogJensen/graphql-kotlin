package graphql.language


import java.math.BigInteger
import java.util.ArrayList

class IntValue(var value: BigInteger) : AbstractNode(), Value {

    override val children: List<Node>
        get() = emptyList()

    override fun isEqualTo(node: Node): Boolean {
        if (this === node) return true
        if (javaClass != node.javaClass) return false

        val that = node as IntValue

        return value == that.value
    }


    override fun toString(): String {
        return "IntValue{" +
                "value=" + value +
                '}'
    }
}
