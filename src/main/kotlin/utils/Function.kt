package cm.daccvo.utils

import cm.daccvo.utils.Constants.Regex.REGEX_EMAIL
import cm.daccvo.utils.Constants.Regex.REGEX_PASSWORD

fun isValidEmail(email: String): Boolean = REGEX_EMAIL.matches(email)

fun isPasswordValid(password: String): Boolean = REGEX_PASSWORD.matches(password)