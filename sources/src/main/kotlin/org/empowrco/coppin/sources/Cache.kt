package org.empowrco.coppin.sources

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.SetArgs
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.coroutines
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import org.empowrco.coppin.utils.serialization.json
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


internal interface Cache {
    suspend fun <T : Any> get(key: String, deserializer: DeserializationStrategy<T>): T?
    suspend fun <T : Any> getList(key: String, deserializer: KSerializer<T>): List<T>
    suspend fun set(key: String, value: String, timeoutInMillis: Long = TimeUnit.DAYS.toMillis(1))
    suspend fun delete(vararg keys: String): Long
    suspend fun contains(key: String): Boolean
}

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class RealCache : Cache {
    private val redisClient = if (System.getenv("DEBUG").toBoolean()) {
        RedisClusterClient.create("${System.getenv("REDIS_URL")}:${System.getenv("REDIS_PORT")}")
    } else {
        RedisClusterClient.create(System.getenv("REDIS_URL"))

    }
    private val connection = redisClient.connect()
    private val syncCommands = connection.coroutines()

    override suspend fun set(key: String, value: String, timeoutInMillis: Long) {
        val setArgs = SetArgs()
        setArgs.px(timeoutInMillis)
        syncCommands.set(key, json.encodeToString(value), setArgs)
    }

    override suspend fun <T : Any> get(key: String, deserializer: DeserializationStrategy<T>): T? {
        return try {
            if (contains(key)) {
                val value = syncCommands.get(key) ?: return null
                try {
                    json.decodeFromString(deserializer, value)
                } catch (ex: Exception) {
                    delete(key)
                    null
                }
            } else {
                null
            }
        } catch (ex: Exception) {
            delete(key)
            null
        }
    }

    override suspend fun <T : Any> getList(key: String, deserializer: KSerializer<T>): List<T> {
        return try {
            if (contains(key)) {
                val value = syncCommands.get(key) ?: return emptyList()
                try {
                    json.decodeFromString(ListSerializer(deserializer), value)
                } catch (ex: Exception) {
                    delete(key)
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (ex: Exception) {
            delete(key)
            emptyList()
        }
    }

    override suspend fun contains(key: String): Boolean {
        return (syncCommands.exists(key) ?: 0) > 0
    }

    override suspend fun delete(vararg keys: String): Long {
        return syncCommands.del(*keys) ?: 0
    }
}

internal class DebugCache : Cache {

    private val cache = mutableMapOf<String, String>()
    val timer = Timer("debug_cache", false)
    override suspend fun <T : Any> get(key: String, deserializer: DeserializationStrategy<T>): T? {
        return if (cache.containsKey(key)) {
            try {
                val value = cache[key]!!
                json.decodeFromString(deserializer, value)
            } catch (ex: Exception) {
                delete(key)
                null
            }
        } else {
            null
        }
    }

    override suspend fun <T : Any> getList(key: String, deserializer: KSerializer<T>): List<T> {
        return if (cache.containsKey(key)) {
            try {
                val value = cache[key]!!
                json.decodeFromString(ListSerializer(deserializer), value)
            } catch (ex: Exception) {
                delete(key)
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override suspend fun set(key: String, value: String, timeoutInMillis: Long) {
        cache[key] = value
        timer.schedule(timerTask {
            cache.remove(key)
        }, timeoutInMillis)
    }

    override suspend fun contains(key: String): Boolean {
        return cache.containsKey(key)
    }

    override suspend fun delete(vararg keys: String): Long {
        var count = 0L
        keys.forEach {
            cache.remove(it)?.let {
                count++
            }
        }
        return count
    }
}
