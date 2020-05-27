package com.ginsberg.counter

import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Set up a redis server for testing.
 * It might be worth exploring TestContainers for this as well.
 */
@TestConfiguration
internal class TestRedisConfiguration {
    private val redisServer = RedisServer(6379)

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }
}