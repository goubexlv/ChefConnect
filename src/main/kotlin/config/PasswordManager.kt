package cm.daccvo.config

import cm.daccvo.utils.Constants.Hash.HASH_ALGO
import cm.daccvo.utils.Constants.Hash.SALT_LENGTH
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordManager {

    fun generateSalt(lg: Int = SALT_LENGTH): String {
        val secureRandom = SecureRandom()
        val salt = ByteArray(lg)
        secureRandom.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(
        algo: String = HASH_ALGO,
        password: String,
        salt: String
    ): String {
        val md = MessageDigest.getInstance(algo)
        val saltBytes = Base64.getDecoder().decode(salt)
        val passwordBytes = password.toByteArray(Charsets.UTF_8)
        val combined = passwordBytes + saltBytes
        val hash = md.digest(combined)
        return Base64.getEncoder().encodeToString(hash)
    }

}