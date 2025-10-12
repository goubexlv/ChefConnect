package cm.daccvo.config

import cm.daccvo.domain.dto.recette.RecettesSearsh
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.domain.recette.Recette
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.ElasticsearchException
import co.elastic.clients.elasticsearch._types.Refresh
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.DeleteResponse
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import java.io.StringReader

class SearchEngine(private val client: ElasticsearchClient) {

    companion object {
        private const val INDEX_NAME = "recettes"
        private val INDEX_SETTINGS = """
               {
                  "settings": {
                    "number_of_shards": 1,
                    "number_of_replicas": 0,
                    "analysis": {
                      "analyzer": {
                        "standard_analyzer": { "type": "standard" },
                        "suggest_analyzer": {
                          "type": "custom",
                          "tokenizer": "standard",
                          "filter": ["lowercase", "asciifolding"]
                        }
                      }
                    }
                  },
                  "mappings": {
                    "properties": {
                      "id": { "type": "keyword" },
                      "uuid": { "type": "keyword" },
                      
                      "title": {
                        "type": "text",
                        "analyzer": "standard",
                        "fields": {
                          "keyword": { "type": "keyword" }
                        }
                      },
                      "title_suggest": {
                        "type": "completion",
                        "analyzer": "suggest_analyzer"
                      },
                      
                      "description": {
                        "type": "text",
                        "analyzer": "standard"
                      },
                      
                      "imagePath": { "type": "keyword" },
                      
                      "category": {
                        "type": "keyword"
                      },
                      
                      "prepaTimes": { "type": "text", "analyzer": "standard" },
                      "cookTime": { "type": "text", "analyzer": "standard" },
                      "serving": { "type": "keyword" }{ "type": "text", "analyzer": "standard" },
                      
                      "difficulty": {
                        "type": "keyword"
                      },
                      
                      "ingredients": {
                        "type": "nested",
                        "properties": {
                          "quantity": { "type": "keyword" },
                          "unit": { "type": "keyword" },
                          "name": {
                            "type": "text",
                            "analyzer": "standard",
                            "fields": {
                              "keyword": { "type": "keyword" }
                            }
                          },
                          "name_suggest": {
                            "type": "completion",
                            "analyzer": "suggest_analyzer"
                          }
                        }
                      },
                      
                      "instruction": {
                        "type": "text",
                        "analyzer": "standard"
                      },
                      
                      "created_at": {
                        "type": "date",
                        "format": "strict_date_optional_time||epoch_millis"
                      },
                      "updated_at": {
                        "type": "date",
                        "format": "strict_date_optional_time||epoch_millis"
                      }
                    }
                  }
                }       
            """.trimIndent()

    }

    fun index(recette: Recette) {
        try {
            val exists = client.indices().exists { e ->
                e.index(INDEX_NAME)
            }.value()

            if (!exists) {
                client.indices().create { c ->
                    c.index(INDEX_NAME)
                        .withJson(StringReader(INDEX_SETTINGS))
                }
                println("Index $INDEX_NAME created successfully")
            }

            // ✅ Ici on envoie l'objet Recette directement
            client.index { idx ->
                idx.index(INDEX_NAME)
                    .id(recette.id)
                    .document(recette) // 👈 directement, sans toDocument()
            }.also {
                println("Indexed recette ${recette.id}")
            }
        } catch (e: Exception) {
            println("Failed to index recette ${recette.id}: ${e.message}")
            e.printStackTrace()
        }
    }

    fun bulkIndex(recettes: List<Recette>) {
        try {
            val exists = client.indices().exists { e ->
                e.index(INDEX_NAME)
            }.value()

            if (!exists) {
                try {
                    client.indices().create { c ->
                        c.index(INDEX_NAME)
                            .withJson(StringReader(INDEX_SETTINGS))
                    }
                    println("Index $INDEX_NAME} created successfully")
                } catch (e: ElasticsearchException) {
                    if (e.response().status() != 400) {
                        throw e
                    }
                    println("Index already exists, continuing...")
                }
            }

            val bulkOperations = recettes.map { recette ->

                BulkOperation.Builder()
                    .index { io ->
                        io.index(INDEX_NAME)
                            .id(recette.id)
                            .document(recette.toDocument())
                    }
                    .build()
            }

            client.bulk { b ->
                b.operations(bulkOperations)
                b.refresh(Refresh.WaitFor)
            }.also {
                if (it.errors()) {
                    it.items().forEach { item ->
                        item.error()?.let { error ->
                            println("Failed to index enterprise ${item.id()}: ${error.reason()}")
                        }
                    }
                } else {
                    println("Successfully indexed ${recettes.size} enterprises")
                }
            }
        } catch (e: Exception) {
            println("Bulk indexing failed ${e.message}")
        }
    }

    fun deleteRecetteByUuid(uuid: String): Boolean {
        return try {
            val searchResponse = client.search({
                it.index(INDEX_NAME)
                    .query { q ->
                        q.term { t ->
                            t.field("uuid").value(uuid)
                        }
                    }
                    .size(1)
            }, Any::class.java)

            if (searchResponse.hits().hits().isEmpty()) {
                println("Aucun document trouvé avec uuid $uuid")
                return false
            }

            val docId = searchResponse.hits().hits()[0].id()
            deleteRecetteById(docId)
        } catch (e: Exception) {
            println("Erreur lors de la recherche par uuid $uuid erreur : ${e.message}")
            false
        }
    }

    fun deleteRecetteById(id: String): Boolean {
        return try {
            val exists = client.exists { e ->
                e.index(INDEX_NAME).id(id)
            }

            if (!exists.value()) {
                println("Tentative de suppression: document $id non trouvé")
                return false
            }

            val response: DeleteResponse = client.delete { delete ->
                delete.index(INDEX_NAME)
                    .id(id)
                    .refresh(Refresh.WaitFor)
            }

            when (response.result().jsonValue()) {
                "deleted" -> {
                    println("Document supprimé avec succès")
                    true
                }
                "not_found" -> {
                    println("Le document n'existait pas")
                    false
                }
                else -> {
                    println("Statut inattendu: ${response.result()}")
                    false
                }
            }
        } catch (e: Exception) {
            println("Échec de la suppression du document erreur : ${e.message}")
            false
        }
    }

    suspend fun searchAdvanced(request: SearchRequest): RecettesSearsh {
        return try {
            val searchRequest = buildSearchRequestWithSuggestions(request)
            val response = client.search(searchRequest, Recette::class.java)

            println("Nombre de résultats trouvés: ${response.hits().total()?.value()}")

            val results = response.hits().hits().mapNotNull { it.source() }
            println("Résultats recettes retournés: ${results.size}")

            val suggestions = extractSuggestionsFromResponse(response)

            RecettesSearsh(recettes = results, suggestions = suggestions, size = request.size, page = request.page)
        } catch (e: Exception) {
            println("Advanced search failed ${e.message}")
            RecettesSearsh(emptyList(), emptyList())
        }
    }

    private fun buildSearchRequestWithSuggestions(request: SearchRequest): co.elastic.clients.elasticsearch.core.SearchRequest {
        val builder = co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
            .index(INDEX_NAME)
            .query(buildQuery(request))
            .from((request.page - 1) * request.size)
            .size(request.size)

        val hasSuggestionTerms = listOf(request.title, request.ingredient)
            .any { it?.isNotBlank() == true }

        if (hasSuggestionTerms) {
            builder.suggest { s ->
                var suggester = s

                request.title?.takeIf { it.isNotBlank() }?.let { title ->
                    suggester = suggester.suggesters("title_suggest") { sug ->
                        sug.text(normalizeSearchTerm(title))
                            .completion { c ->
                                c.field("title_suggest")
                                    .skipDuplicates(true)
                                    .size(5)
                            }
                    }
                }

                request.ingredient?.takeIf { it.isNotBlank() }?.let { ingredient ->
                    suggester = suggester.suggesters("ingredient_suggest") { sug ->
                        sug.text(normalizeSearchTerm(ingredient))
                            .completion { c ->
                                c.field("ingredients.name_suggest")
                                    .skipDuplicates(true)
                                    .size(5)
                            }
                    }
                }

                suggester
            }
        }

        return builder.build()
    }


    private fun buildQuery(request: SearchRequest): Query {
        val mustClauses = mutableListOf<Query>()

        // Recherche plein texte sur le titre
        request.title?.takeIf { it.isNotBlank() }?.let {
            mustClauses += Query.of { q ->
                q.match { m -> m.field("title").query(it) }
            }
        }

        // Recherche dans les ingrédients (nested)
        request.ingredient?.takeIf { it.isNotBlank() }?.let {
            mustClauses += Query.of { q ->
                q.nested { n ->
                    n.path("ingredients")
                    n.query { nq ->
                        nq.match { m -> m.field("ingredients.name").query(it) }
                    }
                }
            }
        }

        // Catégorie exacte
        request.category?.takeIf { it.isNotBlank() }?.let {
            mustClauses += Query.of { q ->
                q.term { t -> t.field("category").value(it) }
            }
        }

        // Difficulté exacte
        request.difficulty?.takeIf { it.isNotBlank() }?.let {
            mustClauses += Query.of { q ->
                q.term { t -> t.field("difficulty").value(it) }
            }
        }

        // Temps de cuisson
        request.cookTime?.takeIf { it.isNotBlank() }?.let {
            mustClauses += Query.of { q ->
                q.term { t -> t.field("cookTime").value(it) }
            }
        }

        // Nombre de portions
        request.serving?.takeIf { it.isNotBlank() }?.let {
            mustClauses += Query.of { q ->
                q.term { t -> t.field("serving").value(it) }
            }
        }

        return if (mustClauses.isEmpty()) {
            Query.of { q -> q.matchAll { m -> m } }
        } else {
            Query.of { q ->
                q.bool { b -> b.must(mustClauses) }
            }
        }
    }


    private fun extractSuggestionsFromResponse(
        response: co.elastic.clients.elasticsearch.core.SearchResponse<Recette>
    ): List<String> {
        val suggestionTexts = mutableSetOf<String>()

        response.suggest()?.forEach { (_, suggestList) ->
            suggestList.forEach { suggest ->
                suggest.completion()?.options()?.forEach { option ->
                    suggestionTexts.add(option.text())
                }
            }
        }

        return suggestionTexts.take(10).toList()
    }

    private fun normalizeSearchTerm(term: String): String =
        term.trim().lowercase()


}