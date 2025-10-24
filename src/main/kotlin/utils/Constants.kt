package cm.daccvo.utils

import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()

object Constants {

    // Fonction utilitaire pour charger une variable d'env avec fallback
    private fun env(name: String, default: String? = null): String =
        dotenv[name]?.takeIf { it.isNotBlank() }
            ?: System.getenv(name)?.takeIf { it.isNotBlank() }
            ?: default
            ?: throw IllegalStateException("Environment variable '$name' is missing")

    private fun envInt(name: String, default: Int? = null): Int =
        env(name, default?.toString()).toInt()

    private fun envLong(name: String, default: Long? = null): Long =
        env(name, default?.toString()).toLong()

    // moderation
    val MODERATION_URL: String = env("MODERATION_URL")

    // Mongo
    val MONGO_USER: String = env("MONGO_USER")
    val MONGO_PASSWORD: String = env("MONGO_PASSWORD")
    val MONGO_HOST: String = env("MONGO_HOST")
    val MONGO_PORT: Int = envInt("MONGO_PORT")
    val MAX_POOL_SIZE: Int = envInt("MAX_POOL_SIZE", 10)
    val DATABASE_NAME: String = env("MONGO_DB")
    val AUTH_COLLECTION: String = env("MONGO_AUTH_COLLECTION")
    val RECETTE_COLLECTION: String = env("MONGO_RECETTE_COLLECTION")
    val AVIS_COLLECTION: String = env("MONGO_AVIS_COLLECTION")

    // JWT
    val JWT_AUDIENCE: String? = env("JWT_AUDIENCE", "")
    val JWT_DOMAIN: String? = env("JWT_DOMAIN", "")
    val JWT_REALM: String = env("JWT_REALM")
    val JWT_ISSUER: String? = env("JWT_ISSUER", "")
    val JWT_SECRET: String? = env("JWT_SECRET", "")

    // MinIO
    val MINIO_URL: String = env("MINIO_URL")
    val MINIO_USER: String = env("MINIO_USER")
    val MINIO_PASSWORD: String = env("MINIO_PASSWORD")
    val MINIO_BUCKET: String = env("MINIO_BUCKET")

    // ElasticSearch
    val ELASTIC_HOST: String = env("ELASTIC_HOST")
    val ELASTIC_APIKEY: String = env("ELASTIC_APIKEY")
    val ELASTIC_PORT: Int = envInt("ELASTIC_PORT", 9200)
    val ELASTIC_CHUNK: Int = envInt("ELASTIC_CHUNK", 500)

    // Redis
    val REDIS_PORT: Int = envInt("REDIS_PORT", 6379)
    val REDIS_TIME: Long = envLong("REDIS_TIME", 60)
    val REDIS_HOST: String = env("REDIS_HOST")

    // --- Sous-objets ---
    object Regex {
        val REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        val REGEX_PHONE = "^[2,6]{1}[2,3,5,6,7,8,9]{1}[0-9]{7}$".toRegex()
        val REGEX_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$".toRegex()
    }

    object Hash {
        val SALT_LENGTH: Int = envInt("SALT_LENGTH", 16)
        val HASH_ALGO: String = env("HASH_ALGO", "SHA-256")
    }


//    // moderation
//    val MODERATION_URL: String = dotenv["MODERATION_URL"]
//
//    // Mongo
//    val MONGO_USER: String = dotenv["MONGO_USER"]
//    val MONGO_PASSWORD: String = dotenv["MONGO_PASSWORD"]
//    val MONGO_HOST: String = dotenv["MONGO_HOST"]
//    val MONGO_PORT: Int = dotenv["MONGO_PORT"].toInt()
//    val MAX_POOL_SIZE: Int = dotenv["MAX_POOL_SIZE"].toInt()
//    val DATABASE_NAME: String = dotenv["MONGO_DB"]
//    val AUTH_COLLECTION: String = dotenv["MONGO_AUTH_COLLECTION"]
//    val RECETTE_COLLECTION: String = dotenv["MONGO_RECETTE_COLLECTION"]
//    val AVIS_COLLECTION: String = dotenv["MONGO_AVIS_COLLECTION"]
//
//    // Variable token JWT
//    val JWT_AUDIENCE: String? = dotenv["JWT_AUDIENCE"]
//    val JWT_DOMAIN: String? = dotenv["JWT_DOMAIN"]
//    val JWT_REALM: String = dotenv["JWT_REALM"]
//    val JWT_ISSUER: String? = dotenv["JWT_ISSUER"]
//    val JWT_SECRET: String? = dotenv["JWT_SECRET"]
//
//    //minio
//    val MINIO_URL: String = dotenv["MINIO_URL"]
//    val MINIO_USER: String = dotenv["MINIO_USER"]
//    val MINIO_PASSWORD: String = dotenv["MINIO_PASSWORD"]
//    val MINIO_BUCKET: String = dotenv["MINIO_BUCKET"]
//
//    val ELASTIC_HOST: String = dotenv["ELASTIC_HOST"]
//    val ELASTIC_PORT: Int = dotenv["ELASTIC_PORT"].toInt()
//    val ELASTIC_CHUNK: Int = dotenv["ELASTIC_CHUNK"].toInt()
//
//    val REDIS_PORT: Int = dotenv["REDIS_PORT"].toInt()
//    val REDIS_TIME: Long = dotenv["REDIS_TIME"].toLong()
//    val REDIS_HOST: String = dotenv["REDIS_HOST"]
//
//
//    object Regex {
//        val REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
//        val REGEX_PHONE = "^[2,6]{1}[2,3,5,6,7,8,9]{1}[0-9]{7}\$".toRegex()
//        // Regex pour validation mot de passe (au moins 8 caractères, une majuscule, une minuscule, un chiffre)
////        val REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$".toRegex()
//        val REGEX_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$".toRegex()
//    }
//
//    object Hash {
//        //        val SALT_LENGTH: Int = System.getenv("SALT_LENGTH").toInt()
////        val HASH_ALGO: String = System.getenv("HASH_ALGO")
//        val SALT_LENGTH: Int = dotenv["SALT_LENGTH"].toInt()
//        val HASH_ALGO: String = dotenv["HASH_ALGO"]
//    }

}