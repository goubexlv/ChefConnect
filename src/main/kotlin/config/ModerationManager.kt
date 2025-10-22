package cm.daccvo.config

import cm.daccvo.domain.recette.ModerationResult
import cm.daccvo.utils.Constants.MODERATION_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ModerationManager {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }


    suspend fun healthCheck(): Boolean {
        return try {
            val response = client.get("$MODERATION_URL/health")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    suspend fun moderateImage(imageBytes: ByteArray, filename: String = "image.jpg"): ModerationResult {
        val response = client.submitFormWithBinaryData(
            url = "$MODERATION_URL/moderate",
            formData = formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                })
            }
        )

        if (response.status != HttpStatusCode.OK) {
            val result = Json.decodeFromString<ModerationResult>(response.body())
            return result
        }else{
            val result = Json.decodeFromString<ModerationResult>(response.body())
            return result
        }

    }

}