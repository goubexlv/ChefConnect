package cm.daccvo.domain.dto.recette

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val title: String? = null,
    val ingredient: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
    val cookTime: String? = null,
    val serving: String? = null,
    val page: Int = 1,
    val size: Int = 10
)
