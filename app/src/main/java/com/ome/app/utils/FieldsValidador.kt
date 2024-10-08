package com.ome.app.utils

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.regex.Pattern

object FieldsValidator {

    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun validateEmail(email: String): Pair<Boolean, String> {
        if (email.trim().isEmpty()) {
            return false to "Please make sure to enter an email"
        }
        if (!isValidEmailString(email)) {
            return false to "Please make sure you're using a valid email"
        }
        return true to "Success"
    }


    fun validatePhone(phone: String): Pair<Boolean, String> {
        if (phone.isNotEmpty()) {
            val phoneUtil = PhoneNumberUtil.getInstance()
            try {
                if (!phoneUtil.isValidNumber(phoneUtil.parse(phone, "US"))
                ) {
                    return false to "Only U.S. phone numbers are supported at this time."
                }
            } catch (e: NumberParseException) {
                return false to "NumberParseException was thrown: $e"
            }
        }
        return true to "Success"
    }

    fun validatePassword(password: String, confirmPassword: String): Pair<Boolean, String> {
        if (password.trim().isEmpty()) {
            return false to "Please make sure to enter a password"
        } else if (confirmPassword.trim().isEmpty()) {
            return false to "Please make sure to enter a confirmation password"
        } else if (password.length <= 8) {
            return false to "Please make sure that your password length is 9 characters or more"
        } else if (confirmPassword.length <= 8) {
            return false to "Please make sure that your confirmation password length is 9 characters or more"
        } else if (password.length > 25) {
            return false to "Please make sure that your password length is less than 26 characters"
        } else if (confirmPassword.length > 25) {
            return false to "Please make sure that your confirmation password length is less than 26 characters"
        } else if (password != confirmPassword) {
            return false to "Please make sure that your passwords match"
        } else {

            // Check for at least three out of the four cases: upper case, lower case, number, special
            var hasUpper = false
            var hasLower = false
            var hasNumber = false
            var hasSpecial = false

            for (char in password) {

                if (char.isUpperCase()) {
                    hasUpper = true
                } else if (char.isLowerCase()) {
                    hasLower = true
                } else if (char.isDigit()) {
                    hasNumber = true
                }
            }

            if (containsSpecialCharacter(password)) {
                hasSpecial = true
            }

            var qualificationsCounter = 0

            if (hasUpper) {
                qualificationsCounter += 1
            }
            if (hasLower) {
                qualificationsCounter += 1
            }
            if (hasNumber) {
                qualificationsCounter += 1
            }
            if (hasSpecial) {
                qualificationsCounter += 1
            }

            if (qualificationsCounter < 3) {
                return false to "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character"
            }
        }

        return true to "Success"
    }


    fun validateFirstAndLastName(firstName: String, lastName: String): Pair<Boolean, String> {
        if (firstName.isEmpty()) {
            return false to "Please make sure to enter a first name."
        }
        if (lastName.isEmpty()) {
            return false to "Please make sure to enter a last name."
        }
        return true to "Success"
    }

    private fun containsSpecialCharacter(sequence: String): Boolean {

        val pattern = Regex("^\\$\\*\\.\\[\\]\\{\\}\\(\\)\\?\"!@#%&/\\,<>':;|_~=+-`")

        return pattern.containsMatchIn(sequence)
    }


    private fun isValidEmailString(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }
}

