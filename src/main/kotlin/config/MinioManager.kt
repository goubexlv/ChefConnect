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




    fun makeBucketPublic() {
        // Politique pour rendre le bucket public en lecture
        val policy = """
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Principal": {"AWS": ["*"]},
                "Action": ["s3:GetObject"],
                "Resource": ["arn:aws:s3:::$MINIO_BUCKET/*"]
            }
        ]
    }
    """.trimIndent()

        minioClient.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(MINIO_BUCKET)
                .config(policy)
                .build()
        )
        println("✅ Bucket $MINIO_BUCKET is now public")
    }

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
                println("✅ Upload success: etag=${response.etag()}")

                // URL publique permanente (sans signature)
                val permanentUrl = "${MINIO_URL}/${MINIO_BUCKET}/${key}"
                println("🌍 Permanent URL: $permanentUrl")
                permanentUrl
            }
        } catch (e: Exception) {
            println("❌ Upload failed: ${e.message}")
            null
        }
    }


}