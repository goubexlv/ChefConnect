package cm.daccvo.config

import cm.daccvo.utils.Constants.MINIO_BUCKET
import cm.daccvo.utils.Constants.MINIO_PASSWORD
import cm.daccvo.utils.Constants.MINIO_URL
import cm.daccvo.utils.Constants.MINIO_USER
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.SetBucketPolicyArgs
import java.io.ByteArrayInputStream

object MinioManager {

    val minioClient = MinioClient.builder()
        .endpoint(MINIO_URL) // ou ton endpoint
        .credentials(MINIO_USER, MINIO_PASSWORD)
        .build()

    fun updateFile(
        key: String,
        bytes: ByteArray,
        contentType: String = "image/jpeg"
    ): String? {
        return try {
            val stream = ByteArrayInputStream(bytes)
            stream.use {
                val response = minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(MINIO_BUCKET)
                        .`object`(key)
                        .stream(stream, bytes.size.toLong(), -1)
                        .contentType(contentType)
                        .build()
                )

                // URL publique permanente (sans signature)
                val permanentUrl = "${MINIO_URL}/${MINIO_BUCKET}/${key}"
                permanentUrl
            }
        } catch (e: Exception) {
            println("❌ Upload failed: ${e.message}")
            null
        }
    }


}