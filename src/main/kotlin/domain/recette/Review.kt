package cm.daccvo.domain.recette

import cm.daccvo.utils.getTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId
import java.util.UUID

@Serializable
data class Review(
    val id: String = ObjectId().toHexString(),
    val uuid: String = UUID.randomUUID().toString(),
    val uuidUser: String,
    val uuidRecette: String,
    val rating: Int = 0,               // 1 à 5
    val comment: String?,
    val createdAt: String = getTime()
){
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Review = json.decodeFromString(document.toJson())

    }
}
