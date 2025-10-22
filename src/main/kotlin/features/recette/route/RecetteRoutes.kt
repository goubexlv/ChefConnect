package cm.daccvo.features.recette.route

import cm.daccvo.config.ModerationManager
import cm.daccvo.domain.Endpoint
import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.recette.RecetteRequest
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.domain.recette.Recette
import cm.daccvo.features.recette.repository.RecetteRepository
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import java.util.UUID

fun Route.recetteRoutes(recetteRepository: RecetteRepository) {


    authenticate {
        post(Endpoint.Recette.path) {
            val request = call.receive<RecetteRequest>()
            val principal = call.principal<JWTPrincipal>()

            val userUuid = principal?.payload?.getClaim("uuid")?.asString()
            if (userUuid == null){
                call.respond(HttpStatusCode.BadRequest, "erreur de l'uuid")
            }

            val response = recetteRepository.createRecette(userUuid.toString(),request)
            if (response.success)
                call.respond(HttpStatusCode.OK, response)
            else
                call.respond(HttpStatusCode.BadRequest, response)
        }

        post(Endpoint.RecetteFile.path){

            var fileName: String? = null
            var fileBytes: ByteArray? = null
            var contentType: String? = null

            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName
                        contentType = part.contentType?.toString()
                        fileBytes = part.streamProvider().readBytes()
                    }
                    else -> part.dispose()
                }
            }

            // Vérification de la présence du fichier
            if (fileName == null || fileBytes == null || contentType == null) {
                call.respond(HttpStatusCode.BadRequest, "Aucun fichier reçu")
                return@post
            }

            // Vérifie que le fichier est bien une image
            if (!contentType!!.startsWith("image/")) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ChefConnectResponse(false, "Seules les images sont autorisées (jpg, png, webp, etc.)")
                )
                return@post
            }

            // Vérifie aussi par extension (sécurité supplémentaire)
            val allowedExtensions = listOf("jpg", "jpeg", "png", "webp")
            val extension = fileName!!.substringAfterLast('.', "").lowercase()

            if (extension !in allowedExtensions) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ChefConnectResponse(false, "Extension non autorisée : $extension")
                )
                return@post
            }

            val moderateur  = ModerationManager.moderateImage(fileBytes!!,contentType!!)

            if (moderateur.is_nsfw) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ChefConnectResponse(false, "L’image semble contenir du contenu inapproprié. Merci de choisir une autre image.")
                )
                return@post
            }

            val uniqueFileName = "${UUID.randomUUID()}_$fileName"

            // ✅ Upload vers MinIO
            val result = recetteRepository.uploadFile(uniqueFileName!!, fileBytes!!,contentType!!)

            if (result.success) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respond(HttpStatusCode.BadRequest, result)
            }

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



}