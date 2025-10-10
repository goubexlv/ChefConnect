package cm.daccvo.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChefConnectResponse (
    val success: Boolean,
    val message: String
)