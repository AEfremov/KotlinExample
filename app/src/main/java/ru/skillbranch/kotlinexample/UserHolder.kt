package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.util.*

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ) : User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                if (map[user.login] == null) map[user.login] = user
                else throw IllegalArgumentException("A user with this email already exists")
            }
    }

    fun loginUser(login: String, password: String) : String? =
        map[login.replace("""[^+\d]""".toRegex(), "").trim()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun registerUserByPhone(fullName: String, rawPhone: String) : User = User.makeUser(fullName = fullName, phone = rawPhone).also { user ->
            if (rawPhone.isNotBlank()) {
                if (!rawPhone.matches("""^(\+7|7|8)?[\s\-]?\(?[489][0-9]{2}\)?[\s\-]?[0-9]{3}[\s\-]?[0-9]{2}[\s\-]?[0-9]{2}${'$'}""".toRegex())) {
                    throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
                } else if (map.keys.contains(rawPhone.replace("""[^+\d]""".toRegex(), ""))) {
                    throw IllegalArgumentException("A user with this phone already exists")
                } else {
                    map[user.login] = user
                }
            }
        }

    fun requestAccessCode(login: String) : Unit {
        map[login.replace("""[^+\d]""".toRegex(), "").trim()]?.requestAccessCode()
    }
}