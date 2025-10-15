package cm.daccvo.features.recette.repository

import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.recette.RecetteRequest
import cm.daccvo.domain.dto.recette.RecettesSearsh
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.domain.recette.Recette

interface RecetteRepository {

    suspend fun createRecette(recette : RecetteRequest) : ChefConnectResponse

    suspend fun updateRecette(uuid: String, recette: RecetteRequest) : ChefConnectResponse

    suspend fun deleteRecette(uuid: String) : ChefConnectResponse

    suspend fun consultationRecette(uuid: String) : Recette?

    suspend fun filterSearch(request: SearchRequest) : RecettesSearsh
    suspend fun SimpleSearch(request: SearchRequest) : RecettesSearsh
}