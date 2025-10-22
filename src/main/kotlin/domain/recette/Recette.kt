package cm.daccvo.domain.recette

import cm.daccvo.domain.users.User
import cm.daccvo.utils.getTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId
import java.util.UUID

@Serializable
data class Recette(
    @SerialName(value = "_id")
    val id: String = ObjectId().toHexString(),
    val uuid: String = UUID.randomUUID().toString(),
    val uuidOwers: String = "",
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
    val rating: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = "",
){
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Recette = json.decodeFromString(document.toJson())
    }
}

