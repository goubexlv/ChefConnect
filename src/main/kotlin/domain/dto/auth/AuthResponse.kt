package cm.daccvo.domain.dto.auth

import cm.daccvo.domain.users.Token
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val success : Boolean,
    val message : String,
    val token : Token? = null
)
