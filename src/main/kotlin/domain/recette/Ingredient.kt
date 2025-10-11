package cm.daccvo.domain.recette

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val quantity : String,
    val unit : String,
    val name : String
)
