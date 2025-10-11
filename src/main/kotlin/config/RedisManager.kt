package cm.daccvo.config

import cm.daccvo.utils.Constants.REDIS_HOST
import cm.daccvo.utils.Constants.REDIS_PORT
import kotlinx.serialization.json.Json
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.util.concurrent.TimeUnit

object RedisManager {

    private const val CACHE_PREFIX = "recette:"
    private const val VILLE_KEY = "${CACHE_PREFIX}ville:"
    private const val CATEGORY_KEY = "${CACHE_PREFIX}category:"
    private const val COMBINED_KEY = "${CACHE_PREFIX}combined:"
    private const val ADVANCED_KEY = "${CACHE_PREFIX}advanced:"
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


    fun clearCache() {
        try {
            pool.resource.use { jedis ->
                jedis.flushAll()
            }
        } catch (e: Exception) {
            // Gérer l'erreur
        }
    }
}