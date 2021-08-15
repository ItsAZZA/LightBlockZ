package com.itsazza.lightblockz.util

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object Cooldown {
    private val cooldowns: Cache<UUID, Long> = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    fun check(playerUUID: UUID, cooldownInSeconds: Int) : Int? {
        val value = cooldowns.getIfPresent(playerUUID)
        val systemTime = System.currentTimeMillis()
        val cooldownMillis = cooldownInSeconds * 1000

        if (value == null || value <= System.currentTimeMillis()) {
            cooldowns.put(playerUUID, systemTime + cooldownMillis)
            return null
        }

        return ((value - systemTime) / 1000.0).roundToInt()
    }
}