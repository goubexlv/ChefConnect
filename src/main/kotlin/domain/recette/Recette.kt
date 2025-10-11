package cm.daccvo.domain.recette

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.UUID

@Serializable
data class Recette(
    @SerialName(value = "_id")
    val id: String = ObjectId().toHexString(),
    val uuid: String = UUID.randomUUID().toString(),
    val title : String,
    val description : String,
    val imagePath : String,
    val category : Category,
    val prepaTimes : String,
    val cookTime : String,
    val serving : String,
    val difficulty : Difficulty,
    val ingredients : List<Ingredient>,
    val instruction : String,

)
