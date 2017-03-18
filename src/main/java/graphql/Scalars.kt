package graphql


import graphql.language.BooleanValue
import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLScalarType

import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

private val LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE)
private val LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE)
private val INT_MAX = BigInteger.valueOf(Int.MAX_VALUE.toLong())
private val INT_MIN = BigInteger.valueOf(Int.MIN_VALUE.toLong())
private val BYTE_MAX = BigInteger.valueOf(Byte.MAX_VALUE.toLong())
private val BYTE_MIN = BigInteger.valueOf(Byte.MIN_VALUE.toLong())
private val SHORT_MAX = BigInteger.valueOf(Short.MAX_VALUE.toLong())
private val SHORT_MIN = BigInteger.valueOf(Short.MIN_VALUE.toLong())

private fun isWholeNumber(input: Any?) = input is Long || input is Int || input is Short || input is Byte

// true if its a number or string that we will attempt to convert to a number via toNumber()
private fun isNumberIsh(input: Any?): Boolean = input is Number || input is String

private fun toNumber(input: Any?) = when (input) {
    is Number -> input
    is String -> input.toDouble()    // Use double as a intermediate Number representation
    else      -> throw AssertException("Unexpected case - this call should be protected by a previous call to isNumberIsh()")
}

private fun verifyRange(input: IntValue, min: BigInteger, max: BigInteger): IntValue {
    val value = input.value
    if (value.compareTo(min) == -1 || value.compareTo(max) == 1) {
        throw GraphQLException("Scalar literal is too big or too small")
    }
    return input

}


val GraphQLInt = GraphQLScalarType("Int", "Built-in Int", object : Coercing<Int?, Int?> {
    override fun serialize(input: Any?): Int? =
            when {
                input is Int       -> input
                isNumberIsh(input) -> toNumber(input).toInt()
                else               -> null
            }

    override fun parseValue(input: Any?): Int? = serialize(input)

    override fun parseLiteral(input: Any?): Int? =
            when (input) {
                is IntValue -> verifyRange(input, INT_MIN, INT_MAX).value.toInt()
                else        -> null
            }
})

val GraphQLLong = GraphQLScalarType("Long", "Long type", object : Coercing<Long?, Long?> {
    override fun serialize(input: Any?): Long? =
            when {
                input is Long      -> input
                isNumberIsh(input) -> toNumber(input).toLong()
                else               -> null
            }

    override fun parseValue(input: Any?): Long? = serialize(input)

    override fun parseLiteral(input: Any?): Long? =
            when (input) {
                is StringValue -> input.value.toLong()
                is IntValue    -> verifyRange(input, LONG_MIN, LONG_MAX).value.toLong()
                else           -> null
            }
})

val GraphQLShort = GraphQLScalarType("Short", "Built-in Short as Int", object : Coercing<Short?, Short?> {
    override fun serialize(input: Any?): Short? =
            when {
                input is Short     -> input
                isNumberIsh(input) -> toNumber(input).toShort()
                else               -> null
            }

    override fun parseValue(input: Any?): Short? = serialize(input)

    override fun parseLiteral(input: Any?): Short? =
            when (input) {
                is StringValue -> input.value.toShort()
                is IntValue    -> verifyRange(input, SHORT_MIN, SHORT_MAX).value.toShort()
                else           -> null
            }
})

var GraphQLByte = GraphQLScalarType("Byte", "Built-in Byte as Int", object : Coercing<Byte?, Byte?> {
    override fun serialize(input: Any?): Byte? =
            when {
                input is Byte      -> input
                isNumberIsh(input) -> toNumber(input).toByte()
                else               -> null
            }

    override fun parseValue(input: Any?): Byte? = serialize(input)

    override fun parseLiteral(input: Any?): Byte? =
            when (input) {
                is StringValue -> input.value.toByte()
                is IntValue    -> verifyRange(input, BYTE_MIN, BYTE_MAX).value.toByte()
                else           -> null
            }
})

val GraphQLFloat = GraphQLScalarType("Float", "Built-in Float", object : Coercing<Number?, Number?> {
    override fun serialize(input: Any?): Number? = when {
        input is Float     -> input //toNumber(input.toString()).toDouble()
        input is Double    -> input
        isNumberIsh(input) -> toNumber(input).toDouble()
        else               -> null
    }

    override fun parseValue(input: Any?): Number? = serialize(input)

    override fun parseLiteral(input: Any?): Number? =
            when (input) {
                is FloatValue -> input.value.toDouble()
                is IntValue   -> input.value.toDouble()
                else          -> null
            }
})

val GraphQLBigInteger = GraphQLScalarType("BigInteger", "Built-in java.math.BigInteger", object : Coercing<BigInteger?, BigInteger?> {
    override fun serialize(input: Any?): BigInteger? =
            when {
                input is BigInteger -> input
                input is String     -> BigInteger(input)
                isNumberIsh(input)  -> BigInteger.valueOf(toNumber(input).toLong())
                else                -> null
            }

    override fun parseValue(input: Any?): BigInteger? = serialize(input)

    override fun parseLiteral(input: Any?): BigInteger? =
            when (input) {
                is StringValue -> BigInteger(input.value)
                is IntValue    -> input.value
                else           -> null
            }
})

val GraphQLBigDecimal = GraphQLScalarType("BigDecimal", "Built-in java.math.BigDecimal", object : Coercing<BigDecimal?, BigDecimal?> {
    override fun serialize(input: Any?): BigDecimal? =
            when {
                input is BigDecimal  -> input
                input is String      -> BigDecimal(input)
                isWholeNumber(input) -> BigDecimal.valueOf(toNumber(input).toLong())
                input is Number      -> BigDecimal.valueOf(toNumber(input).toDouble())
                else                 -> null
            }

    override fun parseValue(input: Any?): BigDecimal? = serialize(input)

    override fun parseLiteral(input: Any?): BigDecimal? =
            when (input) {
                is StringValue -> BigDecimal(input.value)
                is IntValue    -> BigDecimal(input.value)
                is FloatValue  -> input.value
                else           -> null
            }
})

val GraphQLString = GraphQLScalarType("String", "Built-in String", object : Coercing<String?, String?> {
    override fun serialize(input: Any?): String? = input?.toString()

    override fun parseValue(input: Any?): String? = serialize(input)

    override fun parseLiteral(input: Any?): String? =
            when (input) {
                is StringValue -> input.value
                else           -> null
            }
})

val GraphQLStringNonNull = GraphQLNonNull(GraphQLString)

val GraphQLBoolean = GraphQLScalarType("Boolean", "Built-in Boolean", object : Coercing<Boolean?, Boolean?> {
    override fun serialize(input: Any?): Boolean? =
            when (input) {
                is Boolean -> input
                is Int     -> input > 0
                is String  -> input.toBoolean()
                else       -> null
            }

    override fun parseValue(input: Any?): Boolean? = serialize(input)

    override fun parseLiteral(input: Any?): Boolean? =
            when (input) {
                is BooleanValue -> input.value
                else            -> null
            }
})

val GraphQLID = GraphQLScalarType("ID", "Built-in ID", object : Coercing<String?, String?> {
    override fun serialize(input: Any?): String? =
            when (input) {
                is String -> input
                is Int    -> input.toString()
                else      -> null
            }

    override fun parseValue(input: Any?): String? = serialize(input)

    override fun parseLiteral(input: Any?): String? =
            when (input) {
                is StringValue -> input.value
                is IntValue    -> input.value.toString()
                else           -> null
            }
})

val GraphQLChar = GraphQLScalarType("Char", "Built-in Char as Character", object : Coercing<Char?, Char?> {
    override fun serialize(input: Any?): Char? =
            when (input) {
                is Char   -> input
                is String -> if (input.length == 1) input[0] else null
                else      -> null
            }

    override fun parseValue(input: Any?): Char? = serialize(input)

    override fun parseLiteral(input: Any?): Char? =
            when (input) {
                is StringValue -> if (input.value.length == 1) input.value[0] else null
                else           -> null
            }
})

val GraphQLDate = GraphQLScalarType("DateTime", "DateTime type", object : Coercing<Date?, Date?> {
    private val dateFormat = "yyyy-MM-dd'T'HH:mm'Z'"
    private val timeZone = TimeZone.getTimeZone("UTC")

    override fun serialize(input: Any?): Date? {
        when (input) {
            is String -> return parse(input as String?)
            is Date   -> return input
            is Long   -> return Date(input)
            is Int    -> return Date(input.toLong())
            else      -> throw GraphQLException("Wrong timestamp value")
        }
    }

    override fun parseValue(input: Any?): Date? {
        return serialize(input)
    }

    override fun parseLiteral(input: Any?): Date? {
        if (input !is StringValue) return null
        return parse(input.value)
    }

    private fun parse(input: String?): Date {
        try {
            return simpleDateFormat.parse(input)
        } catch (e: Exception) {
            throw GraphQLException("Can not parse input date", e)
        }
    }

    private val simpleDateFormat: SimpleDateFormat
        get() {
            val df = SimpleDateFormat(dateFormat)
            df.timeZone = timeZone
            return df
        }
})

