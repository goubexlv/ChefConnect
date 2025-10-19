package cm.daccvo.plugins

import cm.daccvo.features.auth.repository.AuthRepository
import cm.daccvo.features.auth.routes.authRoutes
import cm.daccvo.features.recette.repository.RecetteRepository
import cm.daccvo.features.recette.route.recetteRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    routing {

        val authRepository : AuthRepository by application.inject()
        val recetteRepository : RecetteRepository by application.inject()

        authRoutes(authRepository)
        recetteRoutes(recetteRepository)
    }
}
