package graphql.relay

import java.nio.charset.Charset
import javax.xml.bind.DatatypeConverter
import java.io.UnsupportedEncodingException


object Base64 {
    fun toBase64(string: String): String {
        try {
            return DatatypeConverter.printBase64Binary(string.toByteArray(charset("utf-8")))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

    }

    fun fromBase64(string: String): String {
        return String(DatatypeConverter.parseBase64Binary(string), Charset.forName("UTF-8"))
    }
}
