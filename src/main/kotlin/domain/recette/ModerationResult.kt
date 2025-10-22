package cm.daccvo.domain.recette

import kotlinx.serialization.Serializable

@Serializable
data class ModerationResult(
    val is_nsfw: Boolean,
    val confidence: Double,
    val categories: Categories,
    val message: String
)

@Serializable
data class Categories(
    val nudity: Double,
    val sexual: Double,
    val explicit: Double,
    val safe: Double
)
