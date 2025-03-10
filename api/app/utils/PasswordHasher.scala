package utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
  def main(args: Array[String]): Unit = {
    val plainPassword = "admin123"
    val hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12))
    println(s"Hashed password: $hashedPassword")
  }
}
