package cm.daccvo.domain.dto.recette

import cm.daccvo.domain.recette.Recette
import cm.daccvo.domain.recette.Review
import kotlinx.serialization.Serializable

@Serializable
data class RecetteResponse(
    val recette : Recette,
    val commentaire : List<Review>
)
