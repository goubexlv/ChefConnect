package cm.daccvo.config

import cm.daccvo.domain.users.Token
import cm.daccvo.utils.Constants.JWT_AUDIENCE
import cm.daccvo.utils.Constants.JWT_DOMAIN
import cm.daccvo.utils.Constants.JWT_SECRET
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

object JwtManager {

    private const val ACCESS_TOKEN_EXPIRATION = 24L // Hours
    private const val REFRESH_TOKEN_EXPIRATION = 30L // Days
    private val algorithm = Algorithm.HMAC256(JWT_SECRET)

    fun generateTokenEmail(userId: String, email: String?, isRefreshToken: Boolean = false): String {
        val expiration = if (isRefreshToken) {
            Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION * 24 * 60 * 60 * 1000)
        } else {
            Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION * 60 * 60 * 1000)
        }

        return JWT.create()
            .withAudience(JWT_AUDIENCE)
            .withIssuer(JWT_DOMAIN)
            .withSubject(userId)
            .withClaim("email", email)
            .withClaim("uuid", userId)
            .withClaim("refresh", isRefreshToken)
            .withIssuedAt(Date())
            .withExpiresAt(expiration)
            .sign(Algorithm.HMAC256(JWT_SECRET))
    }

    fun refreshToken(token: String): Token {
        val verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET))
            .withIssuer(JWT_DOMAIN)
            .build()

        val decoded = try {
            verifier.verify(token)
        } catch (e: JWTVerificationException) {
            throw IllegalArgumentException("Refresh token invalid or expired")
        }

        val isRefresh = decoded.getClaim("refresh").asBoolean()
        if (!isRefresh) throw IllegalArgumentException("Token is not a refresh token")

        val userId = decoded.getClaim("uuid").asString()
        val email = decoded.getClaim("email").asString()
        val phone = decoded.getClaim("phone").asString()

        val newAccessToken =
            generateTokenEmail(userId, email, isRefreshToken = false)

        val newRefreshToken =
            generateTokenEmail(userId, email, isRefreshToken = true)


        return Token(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }


}