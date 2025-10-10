package cm.daccvo.domain.dto.auth


import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
@Serializable
data class RegisterRequest(
    val email : String? = null,
    val password : String = "",
    val code : String = "",
    val createdAt: Long = System.currentTimeMillis()
){
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): RegisterRequest = json.decodeFromString(document.toJson())
    }
}
