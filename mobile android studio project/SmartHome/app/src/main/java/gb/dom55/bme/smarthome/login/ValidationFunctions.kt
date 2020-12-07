package gb.dom55.bme.smarthome.login

import java.util.*

private val commonPasswords = listOf(
        "Password",
        "Password1",
        "Qwertyui",
        "Qwertyuiop",
        "Qwertzui",
        "Qwertzuiop",
        "Iloveyou",
        "Qwerty123",
        "Qwertz123",
        "1q2w3e4r",
        "Abcd1234",
        "Sunshine",
        "Football",
        "Princess"
)

enum class ValidationError {
    PASSWORD_TOO_SHORT,
    PASSWORD_NO_UPPER,
    PASSWORD_NO_LOWER,
    PASSWORD_NO_NUMBER,
    PASSWORD_TOO_COMMON,
    PASSWORD_OK
}

fun isValidEmail(text : String) : Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
}

fun isValidPassword(text : String) : ValidationError {

    when {
        text.length < 10 -> return ValidationError.PASSWORD_TOO_SHORT
        !text.contains(Regex("[A-Z]")) -> return ValidationError.PASSWORD_NO_UPPER
        !text.contains(Regex("[a-z]")) -> return ValidationError.PASSWORD_NO_LOWER
    }

    for (common in commonPasswords) {
        if (text.toLowerCase(Locale.getDefault()).contains(common.toLowerCase(Locale.getDefault())))
            return ValidationError.PASSWORD_TOO_COMMON
    }

    return ValidationError.PASSWORD_OK
}