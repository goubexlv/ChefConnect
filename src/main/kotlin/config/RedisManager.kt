package cm.daccvo.config

import cm.daccvo.domain.dto.recette.RecettesSearsh
import cm.daccvo.domain.dto.recette.SearchRequest
import cm.daccvo.utils.Constants.REDIS_HOST
import cm.daccvo.utils.Constants.REDIS_PORT
import kotlinx.serialization.json.Json
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object RedisManager {

    private const val CACHE_PREFIX = "recette:"
    private const val VILLE_KEY = "${CACHE_PREFIX}ville:"
    private const val CATEGORY_KEY = "${CACHE_PREFIX}category:"
    private const val COMBINED_KEY = "${CACHE_PREFIX}combined:"
    private const val ADVANCED_KEY = "${CACHE_PREFIX}filtersearch:"
    private const val ADVANCED_BLUR_KEY = "${CACHE_PREFIX}advancedBlur:"
    private const val PARAMS_KEY = "${CACHE_PREFIX}params"
    private val TTL = TimeUnit.HOURS.toSeconds(24) // 24 heures en secondes

    private val json = Json { ignoreUnknownKeys = true }

    private val pool: JedisPool by lazy {
        JedisPool(
            JedisPoolConfig().apply {
                maxTotal = 50
                maxIdle = 20
                minIdle = 5
                testOnBorrow = true
                testOnReturn = true
            },
            REDIS_HOST,
            REDIS_PORT,
            2000,
            null,
            0 // database
        )
    }

    fun cacheFilterSearch(
        cacheKey: String,
        results: RecettesSearsh,
        request: SearchRequest
    ) {
        pool.resource.use { jedis ->
            val normalizedKey = normalizeKey(cacheKey)
            val advancedKey = "$ADVANCED_KEY$normalizedKey"

            try {
                // Sérialisation des résultats
                val jsonResults = json.encodeToString(results)
                val dataToStore = if (jsonResults.length > 10_000) {
                    jsonResults.compress()
                } else {
                    jsonResults
                }

                // Stockage atomique Redis (pipeline = + rapide)
                jedis.pipelined().use { pipe ->
                    pipe.setex(advancedKey, TTL, dataToStore)
                    pipe.hset(PARAMS_KEY, normalizedKey, json.encodeToString(request))
                    pipe.sync()
                }

                println("✅ Cached advanced search for key: $cacheKey (${dataToStore.length} chars)")
            } catch (e: Exception) {
                System.err.println(
                    "⚠️ Failed to cache advanced search for key '$cacheKey': ${e.message}"
                )
            }
        }
    }

    fun getFilterSearch(cacheKey: String): RecettesSearsh? {
        val key = "${ADVANCED_KEY}${normalizeKey(cacheKey)}"

        return try {
            pool.resource.use { jedis ->
                val cachedValue = jedis.get(key) ?: return null

                val jsonData = if (cachedValue.length > 10_000) {
                    cachedValue.decompress()
                } else {
                    cachedValue
                }

                // Décodage JSON sécurisé
                return Json.decodeFromString<RecettesSearsh>(jsonData)
            }
        } catch (e: Exception) {
            println("⚠️ Failed to retrieve cached advanced search for key=$cacheKey: ${e.message}")
            null
        }
    }


    fun clearCache() {
        try {
            pool.resource.use { jedis ->
                jedis.flushAll()
            }
        } catch (e: Exception) {
            // Gérer l'erreur
        }
    }

    fun generateCacheKey(request: SearchRequest): String {
        return buildString {
            append("search:")
            request.title?.let { append("title=$it:") }
            request.category?.let { append("cat=$it:") }
            request.difficulty?.let { append("diff=$it:") }
            request.cookTime?.let { append("cook=$it:") }
            request.serving?.let { append("serving=$it:") }
        }.removeSuffix(":")
    }


    private fun normalizeKey(key: String): String = key
        .lowercase()
        .replace(Regex("[éèêë]"), "e")
        .replace(Regex("[àâä]"), "a")
        .replace(Regex("[îï]"), "i")
        .replace(Regex("[ôö]"), "o")
        .replace(Regex("[ùûü]"), "u")
        .replace(Regex("[^a-z0-9]"), "-")
        .trim()

    private fun String.compress(): String {
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            GZIPOutputStream(byteArrayOutputStream).use { gzip ->
                gzip.write(this.toByteArray(Charsets.UTF_8))
            }
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        } catch (e: Exception) {
            // Loguer l'erreur sans interrompre
            println("Compression failed: ${e.message}")
            return this // Retourner la chaîne non compressée en cas d'erreur
        }
    }


    private fun String.decompress(): String {
        try {
            val decodedBytes = Base64.getDecoder().decode(this)
            ByteArrayInputStream(decodedBytes).use { byteInput ->
                GZIPInputStream(byteInput).use { gzipInput ->
                    return gzipInput.readBytes().toString(Charsets.UTF_8)
                }
            }
        } catch (e: Exception) {
            // Loguer l'erreur sans interrompre
            println("Decompression failed: ${e.message}")
            return this // Retourner la chaîne non décompressée en cas d'erreur
        }
    }


}