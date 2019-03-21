package android.support.core.helpers

import java.util.concurrent.TimeUnit

class TemporaryData<K, V>(private val timeout: Long = 60,
                          private val timeUnit: TimeUnit = TimeUnit.SECONDS) {
    private val mCache = hashMapOf<K, V>()
    private val mTime = hashMapOf<K, Long>()

    private fun isValid(key: K): Boolean {
        if (!mCache.containsKey(key)) return false
        return System.currentTimeMillis() - (mTime[key] ?: 0) < timeUnit.toMillis(timeout)
    }

    operator fun get(key: K): V? {
        return if (isValid(key)) mCache[key]!! else null
    }

    operator fun set(key: K, value: V) {
        mCache[key] = value
        mTime[key] = System.currentTimeMillis()
    }

    fun getOrLoad(key: K, function: () -> V) =
        this[key] ?: function().apply { this@TemporaryData[key] = this }
}
