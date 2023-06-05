package com.example.connect.utils

import java.util.regex.Pattern

object FieldsValidator {
    private const val EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    fun validateEmail(email: String): Boolean {
        val pattern = Pattern.compile(EMAIL_REGEX)
        val matcher = pattern.matcher(email)
        if (!matcher.matches())
            return false
        return true
    }
}