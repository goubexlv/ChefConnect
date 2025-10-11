package cm.daccvo.utils

import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()

object Constants {

    // Variable token JWT
    val JWT_AUDIENCE: String? = dotenv["JWT_AUDIENCE"]
    val JWT_DOMAIN: String? = dotenv["JWT_DOMAIN"]
    val JWT_REALM: String = dotenv["JWT_REALM"]
    val JWT_ISSUER: String? = dotenv["JWT_ISSUER"]
    val JWT_SECRET: String? = dotenv["JWT_SECRET"]

    val MONGO_USER: String = dotenv["MONGO_USER"]
    val MONGO_PASSWORD: String = dotenv["MONGO_PASSWORD"]
    val MONGO_HOST: String = dotenv["MONGO_HOST"]
    val MONGO_PORT: Int = dotenv["MONGO_PORT"].toInt()
    val MAX_POOL_SIZE: Int = dotenv["MAX_POOL_SIZE"].toInt()
    val DATABASE_NAME: String = dotenv["MONGO_DB"]
    val AUTH_COLLECTION: String = dotenv["MONGO_AUTH_COLLECTION"]
    val RECETTE_COLLECTION: String = dotenv["MONGO_RECETTE_COLLECTION"]

    val ELASTIC_HOST: String = dotenv["ELASTIC_HOST"]
    val ELASTIC_PORT: Int = dotenv["ELASTIC_PORT"].toInt()
    val ELASTIC_CHUNK: Int = dotenv["ELASTIC_CHUNK"].toInt()


    object Regex {
        val REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        val REGEX_PHONE = "^[2,6]{1}[2,3,5,6,7,8,9]{1}[0-9]{7}\$".toRegex()
        // Regex pour validation mot de passe (au moins 8 caractères, une majuscule, une minuscule, un chiffre)
//        val REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$".toRegex()
        val REGEX_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$".toRegex()
    }

    object Hash {
        //        val SALT_LENGTH: Int = System.getenv("SALT_LENGTH").toInt()
//        val HASH_ALGO: String = System.getenv("HASH_ALGO")
        val SALT_LENGTH: Int = dotenv["SALT_LENGTH"].toInt()
        val HASH_ALGO: String = dotenv["HASH_ALGO"]
    }

}