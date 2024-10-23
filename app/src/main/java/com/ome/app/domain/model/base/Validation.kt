package com.ome.app.domain.model.base

import com.google.android.material.textfield.TextInputLayout

enum class Validation{
    OLD_PASSWORD,
    NEW_PASSWORD,
    FIRST_NAME,
    LAST_NAME,
    ALL_FIELDS,
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
    return when{
        trim().isEmpty() -> ResponseWrapper.Error(DefaultValidation.REQUIRED)
        !hasUpper -> ResponseWrapper.Error("Doesn't contains uppercase letter.")
        !hasLower -> ResponseWrapper.Error("Doesn't contains lowercase letter.")
        !hasNumber -> ResponseWrapper.Error("Doesn't contains number.")
        !isGraterThan8 -> ResponseWrapper.Error("Length is smaller than 9.")
        isGraterThan25 -> ResponseWrapper.Error("Length is greater than 25.")
        else -> ResponseWrapper.Success(true)
    }
}

object DefaultValidation{
    const val REQUIRED = "Required field."
}