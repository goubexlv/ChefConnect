package cm.daccvo.features.recette.repository

import cm.daccvo.config.ElasticsearchManager
import cm.daccvo.config.MongoDbManager
import cm.daccvo.config.SearchEngine
import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.recette.RecetteRequest
import cm.daccvo.domain.recette.Recette

class RecetteRepositoryImpl : RecetteRepository {

    private val recetteDocument = MongoDbManager.recette
    private val elasticClient = ElasticsearchManager.createClient()
    private val searchEngine = SearchEngine(elasticClient)

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
                searchEngine.index(recetteDoc)
                ChefConnectResponse(true,"Recette enregistrer avec success")
            } else {
                ChefConnectResponse(false,"Échec de l'enregistrement")
            }

        } catch (e : Exception) {
            ChefConnectResponse(false,"Échec de l'enregistrement: ${e.message}")
        }

    }

    override suspend fun updateRecette(recette: RecetteRequest): ChefConnectResponse {
        return try {
            ChefConnectResponse(true,"Recette enregistrer avec success")
        } catch (e : Exception) {
            ChefConnectResponse(false,"Échec du update: ${e.message}")
        }
    }
}