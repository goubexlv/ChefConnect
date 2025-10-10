package cm.daccvo.plugins

import cm.daccvo.features.auth.repository.AuthRepository
import cm.daccvo.features.auth.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    routing {

        val authRepository : AuthRepository by application.inject()

        authRoutes(authRepository)
    }
}
