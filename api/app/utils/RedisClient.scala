package utils

import redis.clients.jedis._

object RedisClient {
  private val redisHost = "localhost"
  private val redisPort = 6379
  private val jedis = new Jedis(redisHost, redisPort)

  def set(key: String, value: String, expireTime: Int = 3600): Unit = {
    jedis.setex(key, expireTime, value)
  }

  def get(key: String): String = {
    jedis.get(key)
  }

  def delete(key: String): Unit = {
    jedis.del(key)
  }
}
