package cm.daccvo.features.recette.repository

import cm.daccvo.config.ElasticsearchManager
import cm.daccvo.config.MongoDbManager
import cm.daccvo.config.RedisManager
import cm.daccvo.config.RedisManager.generateCacheKey
import cm.daccvo.config.SearchEngine
import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.recette.RecetteRequest
import cm.daccvo.domain.dto.recette.RecettesSearsh
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.domain.recette.Recette
import cm.daccvo.utils.getTime
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class RecetteRepositoryImpl : RecetteRepository {

    private val recetteDocument = MongoDbManager.recette
    private val elasticClient = ElasticsearchManager.createClient()
    private val searchEngine = SearchEngine(elasticClient)
    private val redisCache = RedisManager

    override suspend fun createRecette(recette: RecetteRequest): ChefConnectResponse {

        return try {
            val recetteDoc = Recette(
                title = recette.title,
                description = recette.description,
                imagePath = recette.imagePath,
                category = recette.category,
                prepaTimes = recette.prepaTimes,
                cookTime = recette.cookTime,
                serving = recette.serving,
                difficulty = recette.difficulty,
                ingredients = recette.ingredients,
                instruction = recette.instruction
            )

            if(recetteDocument.insertOne(recetteDoc.toDocument()).wasAcknowledged()){
                searchEngine.bulkIndex(listOf(recetteDoc))
                redisCache.clearCache()
                ChefConnectResponse(true,"Recette enregistrer avec success")
            } else {
                ChefConnectResponse(false,"Échec de l'enregistrement")
            }

        } catch (e : Exception) {
            ChefConnectResponse(false,"Échec de l'enregistrement: ${e.message}")
        }

    }

    override suspend fun updateRecette(uuid: String, recette: RecetteRequest): ChefConnectResponse {
        recetteDocument.find(eq("uuid", uuid)).firstOrNull()
            ?: ChefConnectResponse(false,"Recette n'existe pas")

        return try {

            val updateRecette = Document().apply {
                recette.title.takeIf { it.isNotBlank() }?.let { put("title", it) }
                recette.description.takeIf { it.isNotBlank() }?.let { put("description", it) }
                recette.imagePath.takeIf { it.isNotBlank() }?.let { put("imagePath", it) }
                recette.prepaTimes.takeIf { it.isNotBlank() }?.let { put("prepaTimes", it) }
                recette.cookTime.takeIf { it.isNotBlank() }?.let { put("cookTime", it) }
                recette.serving.takeIf { it.isNotBlank() }?.let { put("serving", it) }

                // Enum: Category & Difficulty
                put("category", recette.category.name)
                put("difficulty", recette.difficulty.name)

                // Liste des ingrédients
                recette.ingredients.takeIf { it.isNotEmpty() }?.let { ingredients ->
                    put("ingredients", ingredients.map {
                        Document().apply {
                            put("quantity", it.quantity)
                            put("unit", it.unit)
                            put("name", it.name)
                        }
                    })
                }

                // Instructions
                recette.instruction.takeIf { it.isNotBlank() }?.let { put("instruction", it) }

                // Dates
                put("updatedAt", getTime())
            }

            if(recetteDocument.updateOne(eq("uuid", uuid), Document("\$set", updateRecette)).wasAcknowledged()) {

                val recette = recetteDocument.find(eq("uuid", uuid)).firstOrNull()
                recette?.let { Recette.fromDocument(it) }?.let { searchEngine.bulkIndex(listOf(it)) }
                redisCache.clearCache()
                ChefConnectResponse(true,"Recette update avec success")
            } else {
                ChefConnectResponse(false,"Échec de l'update")
            }

        } catch (e : Exception) {
            ChefConnectResponse(false,"Échec du update: ${e.message}")
        }
    }

    override suspend fun deleteRecette(uuid: String): ChefConnectResponse {
        recetteDocument.find(eq("uuid", uuid)).firstOrNull()
            ?: ChefConnectResponse(false,"Recette n'existe pas")

        return try {

            if(recetteDocument.deleteOne(eq("uuid", uuid)).wasAcknowledged()) {
                searchEngine.deleteRecetteByUuid(uuid)
                redisCache.clearCache()
                ChefConnectResponse(true,"Recette update avec success")
            } else {
                ChefConnectResponse(false,"Échec de la suppression")
            }

        } catch (e : Exception) {
            ChefConnectResponse(false,"Échec du update: ${e.message}")
        }
    }

    override suspend fun consultationRecette(uuid: String): Recette? {
        val recette = recetteDocument.find(eq("uuid", uuid)).firstOrNull()
            ?: null

        return try {
            if (recette != null){
                Recette.fromDocument(recette)
            } else {
                null
            }
        } catch (e : Exception) {
            null
        }
    }

    override suspend fun filterSearch(request: SearchRequest): RecettesSearsh {
        val cacheKey = generateCacheKey(request)
        val result = redisCache.getFilterSearch(cacheKey)
        if(result != null){
            return result
        } else {
            // 2. Recherche Elasticsearch
            val results = searchEngine.searchAdvanced(request)
            // 3. Mise en cache
            redisCache.cacheFilterSearch(cacheKey, results, request)

            return results
        }

    }

    override suspend fun SimpleSearch(request: SearchRequest): RecettesSearsh {
        val cacheKey = generateCacheKey(request)
        val result = redisCache.getSearch(cacheKey)
        if(result != null){
            return result
        } else {
            // 2. Recherche Elasticsearch
            val results = searchEngine.simpleSearch(request)
            // 3. Mise en cache
            redisCache.cacheSearch(cacheKey, results, request)

            return results
        }

    }

}