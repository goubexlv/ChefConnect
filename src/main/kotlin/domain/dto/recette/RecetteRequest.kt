package cm.daccvo.domain.dto.recette

import cm.daccvo.domain.recette.Category
import cm.daccvo.domain.recette.Difficulty
import cm.daccvo.domain.recette.Ingredient
import kotlinx.serialization.Serializable

@Serializable
data class RecetteRequest(
    val title : String,
    val description : String = "",
    val imagePath : String = "",
    val category : Category,
    val prepaTimes : String,
    val cookTime : String,
    val serving : String,
    val difficulty : Difficulty,
    val ingredients : List<Ingredient>,
    val instruction : String
)
