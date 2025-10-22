package cm.daccvo.features.auth.routes

import cm.daccvo.config.JwtManager.refreshToken
import cm.daccvo.domain.Endpoint
import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.auth.AuthResponse
import cm.daccvo.domain.dto.auth.LoginRequest
import cm.daccvo.domain.dto.auth.RegisterRequest
import cm.daccvo.domain.users.Token
import cm.daccvo.features.auth.repository.AuthRepository
import cm.daccvo.utils.isPasswordValid
import cm.daccvo.utils.isValidEmail
import io.ktor.http.HttpStatusCode
import io.ktor.http.invoke
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.authRoutes(authRepository: AuthRepository){

    post(Endpoint.Register.path) {

        try {
            val request = call.receive<RegisterRequest>()

            if (request.email != null && !isValidEmail(request.email)) {
                call.respond(HttpStatusCode.BadRequest, ChefConnectResponse(false,"Email invalide"))
                return@post
            }

            if (!isPasswordValid(request.password)) {
                call.respond(message = ChefConnectResponse(false,"Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre"), status = HttpStatusCode.BadRequest)
                return@post
            }

            val result = authRepository.register(request)
            if (result.success){
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respond(HttpStatusCode.BadRequest, result)
            }
        } catch (e : Exception){
            call.respond(HttpStatusCode.BadRequest,  ChefConnectResponse(false,"Requête invalide erreur : ${e.message}") )
        }
    }

    post(Endpoint.Login.path) {

        try {
            val request = call.receive<LoginRequest>()
            if (request.email != null && !isValidEmail(request.email)) {
                call.respond(HttpStatusCode.BadRequest, AuthResponse(false,"Email invalide"))
                return@post
            }

            val result = authRepository.login(request)
            if (result.success){
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respond(HttpStatusCode.BadRequest, result)
            }

        } catch (e : Exception) {
            call.respond(HttpStatusCode.BadRequest, AuthResponse(false, "Requête invalide erreur : ${e.message}"))
        }
    }

    post(Endpoint.RefreshToken.path) {
        val body = call.receive<Map<String, String>>()
        val refreshToken = body["refreshToken"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing refresh_token")

        try {
            val tokens = refreshToken(refreshToken)
            call.respond(Token(tokens.accessToken, tokens.refreshToken))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.Unauthorized, e.message ?: "Invalid refresh token")
        }
    }


}