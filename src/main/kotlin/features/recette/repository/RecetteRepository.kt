package cm.daccvo.features.recette.repository

import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.recette.RecetteRequest
import cm.daccvo.domain.dto.recette.RecetteResponse
import cm.daccvo.domain.dto.recette.RecettesSearsh
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.domain.recette.Recette

interface RecetteRepository {

    suspend fun uploadFile(key: String, bytes: ByteArray,contentType: String) : ChefConnectResponse
    suspend fun createRecette(uuidOwers : String, recette : RecetteRequest) : ChefConnectResponse

    suspend fun updateRecette(uuid: String, recette: RecetteRequest) : ChefConnectResponse

    suspend fun deleteRecette(uuid: String) : ChefConnectResponse

    suspend fun consultationRecette(uuid: String) : RecetteResponse?

    suspend fun filterSearch(request: SearchRequest) : RecettesSearsh
    suspend fun SimpleSearch(request: SearchRequest) : RecettesSearsh

    suspend fun addAvis(uuidUser : String, uuidRecette: String, note:Int? = null, comment: String? = null) : ChefConnectResponse
}