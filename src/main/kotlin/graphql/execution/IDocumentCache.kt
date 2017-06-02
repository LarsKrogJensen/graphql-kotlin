package graphql.execution

import graphql.GraphQLError
import graphql.language.Document
import java.util.concurrent.CompletableFuture

interface IDocumentCache {
    fun get(query: String): CompletableFuture<DocumentCacheResult>
    fun set(query: String, document: Document?, errors: List<GraphQLError>?)
}

data class DocumentCacheResult(
    val cacheHit: Boolean,
    val document: Document? = null,
    val errors: List<GraphQLError>? = null
)