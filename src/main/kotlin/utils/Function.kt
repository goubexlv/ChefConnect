package cm.daccvo.utils

import cm.daccvo.utils.Constants.Regex.REGEX_EMAIL
import cm.daccvo.utils.Constants.Regex.REGEX_PASSWORD
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun isValidEmail(email: String): Boolean = REGEX_EMAIL.matches(email)

fun isPasswordValid(password: String): Boolean = REGEX_PASSWORD.matches(password)

fun getTime() : String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val createdAt: String = LocalDateTime.now().format(formatter)
    return createdAt
}