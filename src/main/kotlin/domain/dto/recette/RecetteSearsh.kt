package cm.daccvo.domain.dto.recette

import cm.daccvo.domain.recette.Recette
import kotlinx.serialization.Serializable

@Serializable
data class RecettesSearsh(
    val recettes : List<Recette>,
    val suggestions: List<String>
)
