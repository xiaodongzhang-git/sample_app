package utils

import com.auth0.jwt.{JWT, JWTVerifier}
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

case class JwtPayload(username: String, role: String)

object JwtUtils {
  private val secretKey = "your-secret-key"
  private val algorithm = Algorithm.HMAC256(secretKey)
  private val expirationTime: Long = 6 * 60 * 60 * 1000 // 6h

  /** carete Token */
  def generateToken(username: String, role: String): String = {
    val now = System.currentTimeMillis()
    JWT.create()
      .withSubject(username)
      .withClaim("role", role)
      .withIssuedAt(new Date(now))
      .withExpiresAt(new Date(now + expirationTime))
      .sign(algorithm)
  }

  /** check Token，return JwtPayload */
  def verifyToken(token: String): Option[JwtPayload] = {
    try {
      val verifier: JWTVerifier = JWT.require(algorithm).build()
      val decodedJWT = verifier.verify(token)
      Some(JwtPayload(
        decodedJWT.getSubject,
        decodedJWT.getClaim("role").asString()
      ))
    } catch {
      case _: Exception => None
    }
  }
}
