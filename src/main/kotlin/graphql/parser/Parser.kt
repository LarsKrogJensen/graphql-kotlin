package graphql.parser

import graphql.language.Document
import graphql.parser.antlr.GraphqlLexer
import graphql.parser.antlr.GraphqlParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.ParseCancellationException



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


        val stop = document.getStop()
        val allTokens = tokens.tokens
        if (stop != null && allTokens != null && !allTokens.isEmpty()) {
            val last = allTokens[allTokens.size - 1]
            //
            // do we have more tokens in the stream than we consumed in the parse?
            // if yes then its invalid.  We make sure its the same channel
            val notEOF = last.type != Token.EOF
            val lastGreaterThanDocument = last.tokenIndex > stop.tokenIndex
            val sameChannel = last.channel == stop.channel
            if (notEOF && lastGreaterThanDocument && sameChannel) {
                throw ParseCancellationException("There are more tokens in the query that have not been consumed")
            }
        }
        return antlrToLanguage.result
    }
}
