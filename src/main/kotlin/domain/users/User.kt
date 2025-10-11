package cm.daccvo.domain.users

import cm.daccvo.utils.getTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class User(
    @SerialName(value = "_id")
    val id: String = ObjectId().toHexString(),
    val uuid: String = UUID.randomUUID().toString(),
    val userName: String,
    val email: String,
    val avatar: String? = null,
    val password: String,
    val salt: String,
    val isEmailVerified: Boolean = false,
    val createdAt: String = getTime(),
    val updatedAt: String = getTime(),
){
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): User = json.decodeFromString(document.toJson())
    }
}
