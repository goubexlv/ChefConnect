package cm.daccvo.plugins

import cm.daccvo.utils.Constants.JWT_AUDIENCE
import cm.daccvo.utils.Constants.JWT_DOMAIN
import cm.daccvo.utils.Constants.JWT_REALM
import cm.daccvo.utils.Constants.JWT_SECRET
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain

    authentication {
        jwt {
            realm = JWT_REALM
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience(JWT_AUDIENCE)
                    .withIssuer(JWT_DOMAIN)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("uuid").asString()
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
