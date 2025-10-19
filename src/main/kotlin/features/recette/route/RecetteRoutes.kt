package cm.daccvo.features.recette.route

import cm.daccvo.domain.Endpoint
import cm.daccvo.domain.dto.recette.RecetteRequest
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.domain.recette.Recette
import cm.daccvo.features.recette.repository.RecetteRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.recetteRoutes(recetteRepository: RecetteRepository) {

    post(Endpoint.Recette.path){

        val request = call.receive<RecetteRequest>()

        val response = recetteRepository.createRecette(request)
        if (response.success)
            call.respond(HttpStatusCode.OK, response)
        else
            call.respond(HttpStatusCode.BadRequest, response)

    }

    put(Endpoint.UpdateRecette.path){
        val uuid = call.parameters["uuid"]
        val request = call.receive<RecetteRequest>()

        if(uuid == null) {
            call.respond(HttpStatusCode.BadRequest, "il manque des paramettre")
        }

        val response = recetteRepository.updateRecette(uuid.toString(), request)
        if (response.success)
            call.respond(HttpStatusCode.OK, response)
        else
            call.respond(HttpStatusCode.BadRequest, response)

    }

    delete(Endpoint.DeleteRecette.path){
        val uuid = call.parameters["uuid"]

        if(uuid == null) {
            call.respond(HttpStatusCode.BadRequest, "il manque des paramettre")
        }

        val response = recetteRepository.deleteRecette(uuid.toString())
        if (response.success)
            call.respond(HttpStatusCode.OK, response)
        else
            call.respond(HttpStatusCode.BadRequest, response)

    }

    get(Endpoint.ConsulteRecette.path){
        val uuid = call.parameters["uuid"]

        if(uuid == null) {
            call.respond(HttpStatusCode.BadRequest, "il manque des paramettre")
        }

        val response = recetteRepository.consultationRecette(uuid.toString())
        if (response != null)
            call.respond(HttpStatusCode.OK, response)
        else
            call.respond(HttpStatusCode.BadRequest, response)
    }

    get(Endpoint.FiltreSearch.path){

        val request = SearchRequest(
            title = call.request.queryParameters["title"] ?: "",
            ingredient = call.request.queryParameters["ingredient"] ?: "",
            category = call.request.queryParameters["category"] ?: "",
            difficulty = call.request.queryParameters["difficulty"] ?: "",
            cookTime = call.request.queryParameters["cookTime"] ?: "",
            serving = call.request.queryParameters["serving"] ?: "",
        )

        try {
            val response = recetteRepository.filterSearch(request)
            call.respond(response)
        } catch (e : Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Échec: ${e.message}")

        }

    }

    get(Endpoint.Search.path){

        val request = SearchRequest(
            title = call.request.queryParameters["title"] ?: "",
            ingredient = call.request.queryParameters["ingredient"] ?: "",
            category = call.request.queryParameters["category"] ?: "",
            difficulty = call.request.queryParameters["difficulty"] ?: "",
            cookTime = call.request.queryParameters["cookTime"] ?: "",
            serving = call.request.queryParameters["serving"] ?: "",
        )

        try {
            val response = recetteRepository.SimpleSearch(request)
            call.respond(response)
        } catch (e : Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Échec: ${e.message}")

        }

    }

    get(Endpoint.AddAvis.path) {
        val uuidUser = call.request.queryParameters["uuidUser"]
        val uuidRecette = call.request.queryParameters["uuidRecette"]
        val rating = call.request.queryParameters["rating"]
        val comment = call.request.queryParameters["comment"]

        val note = rating?.toIntOrNull()

        if (uuidUser.isNullOrBlank() || uuidRecette.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "parametre manquant")
            return@get
        }

        try {
            val response = recetteRepository.addAvis(uuidUser,uuidRecette,note,comment)
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Erreur: ${e.message}")
        }

    }

}