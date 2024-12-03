package com.ome.app.domain.model.base

import com.google.android.material.textfield.TextInputLayout

enum class Validation{
    OLD_PASSWORD,
    NEW_PASSWORD,
    RE_PASSWORD,
    FIRST_NAME,
    LAST_NAME,
    ALL_FIELDS,
    EMAIL,
    PHONE
}

var TextInputLayout.errorPassword: String?
    get() = null
    set(message) {
        error = message
        errorIconDrawable = null
        endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
    }

fun String.isValidPasswordResult(): ResponseWrapper<Boolean> {
    val hasUpper = this.any { it.isUpperCase() }
    val hasLower = this.any { it.isLowerCase() }
    val hasNumber = this.any { it.isDigit() }
    val isGraterThan8 = this.length > 8
    val isGraterThan25 = this.length > 25
//    val hasSpecial = this.any { "!@#\$%^&*()-_=+[]{};:'\",.<>?/\\|`~".contains(it) }
    return when {
        trim().isEmpty() -> ResponseWrapper.Error(DefaultValidation.REQUIRED)
        !hasUpper -> ResponseWrapper.Error("Doesn't contain uppercase letter.")
        !hasLower -> ResponseWrapper.Error("Doesn't contain lowercase letter.")
        !hasNumber -> ResponseWrapper.Error("Doesn't contain number.")
        !isGraterThan8 -> ResponseWrapper.Error("Must have 9 characters or more.")
        isGraterThan25 -> ResponseWrapper.Error("Must not have more than 25 characters.")
        else -> ResponseWrapper.Success(true)
    }
}

object DefaultValidation{
    const val REQUIRED = "Required field."
    const val INVALID_EMAIL = "Invalid email."
    const val INVALID_PHONE = "US phone numbers only."
}