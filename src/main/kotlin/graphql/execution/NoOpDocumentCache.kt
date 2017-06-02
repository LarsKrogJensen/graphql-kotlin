package graphql.execution

import graphql.GraphQLError
import graphql.language.Document
import java.util.concurrent.CompletableFuture

class NoOpDocumentCache : IDocumentCache {
    override fun set(query: String, document: Document?, errors: List<GraphQLError>?) {
    }

    override fun get(query: String): CompletableFuture<DocumentCacheResult> {
        return CompletableFuture.completedFuture(DocumentCacheResult(false))
    }

    companion object {
        val INSTANCE = NoOpDocumentCache()
    }
}