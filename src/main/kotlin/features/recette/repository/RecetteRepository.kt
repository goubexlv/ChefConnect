package cm.daccvo.features.recette.repository

import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.recette.RecetteRequest

interface RecetteRepository {

    suspend fun createRecette(recette : RecetteRequest):ChefConnectResponse

    suspend fun updateRecette(uuid: String, recette: RecetteRequest):ChefConnectResponse
}