package graphql.parser

import graphql.language.Document
import graphql.parser.antlr.GraphqlLexer
import graphql.parser.antlr.GraphqlParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.atn.PredictionMode

class Parser {
    fun parseDocument(input: String): Document {

        val lexer = GraphqlLexer(ANTLRInputStream(input))

        val tokens = CommonTokenStream(lexer)

        val parser = GraphqlParser(tokens).apply {
            removeErrorListeners()
            interpreter.predictionMode = PredictionMode.SLL
            errorHandler = BailErrorStrategy()
        }

        val document = parser.document()

        val antlrToLanguage = GraphqlAntlrToLanguage()
        antlrToLanguage.visitDocument(document)
        return antlrToLanguage.result
    }
}
