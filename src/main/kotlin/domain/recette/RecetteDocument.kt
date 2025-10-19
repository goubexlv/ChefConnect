package cm.daccvo.domain.recette

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.UUID

@Serializable
data class RecetteDocument @JsonCreator constructor(
    @JsonProperty("id")
    val id: String = ObjectId().toHexString(),
    @JsonProperty("uuid")
    val uuid: String = UUID.randomUUID().toString(),
    @JsonProperty("title")
    val title: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("imagePath")
    val imagePath: String,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("prepaTimes")
    val prepaTimes: String,
    @JsonProperty("cookTime")
    val cookTime: String,
    @JsonProperty("serving")
    val serving: String,
    @JsonProperty("difficulty")
    val difficulty: String,
    @JsonProperty("ingredients")
    val ingredients: List<IngredientDocument>,
    @JsonProperty("instruction")
    val instruction: String,
    @JsonProperty("createdAt")
    val createdAt: String,
    @JsonProperty("updatedAt")
    val updatedAt: String,
    @JsonProperty("title_suggest")
    val title_suggest: CompletionField? = null,
    @JsonProperty("ingredients_suggest")
    val ingredients_suggest: CompletionField? = null,
    @JsonProperty("category_suggest")
    val category_suggest: CompletionField? = null
) {

    companion object {
        fun fromRecette(recette: Recette): RecetteDocument {
            return RecetteDocument(
                id = recette.id,
                uuid = recette.uuid,
                title = recette.title,
                description = recette.description,
                imagePath = recette.imagePath,
                category = recette.category.name,
                prepaTimes = recette.prepaTimes,
                cookTime = recette.cookTime,
                serving = recette.serving,
                difficulty = recette.difficulty.name,
                ingredients = recette.ingredients.map {
                    IngredientDocument(it.quantity, it.unit, it.name)
                },
                instruction = recette.instruction,
                createdAt = recette.createdAt,
                updatedAt = recette.updatedAt,
                title_suggest = CompletionField(listOf(recette.title)),
                ingredients_suggest = CompletionField(recette.ingredients.map { it.name }),
                category_suggest = CompletionField(listOf(recette.category.name))
            )
        }
    }

    fun toRecette(): Recette {
        return Recette(
            id = this.id,
            uuid = this.uuid,
            title = this.title,
            description = this.description,
            imagePath = this.imagePath,
            category = Category.valueOf(this.category),
            prepaTimes = this.prepaTimes,
            cookTime = this.cookTime,
            serving = this.serving,
            difficulty = Difficulty.valueOf(this.difficulty),
            ingredients = this.ingredients.map {
                Ingredient(it.quantity, it.unit, it.name)
            },
            instruction = this.instruction,
            createdAt = this.createdAt,
            updatedAt = this.createdAt
        )
    }

    @Serializable
    data class IngredientDocument(
        @JsonProperty("quantity")
        val quantity: String,
        @JsonProperty("unit")
        val unit: String,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("name_suggest")
        val name_suggest: CompletionField? = null
    )

    @Serializable
    data class CompletionField(
        @JsonProperty("input")
        val input: List<String>
    )
}


