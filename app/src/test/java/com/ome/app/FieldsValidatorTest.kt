package com.ome.app

import com.ome.app.utils.FieldsValidator
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class FieldsValidatorTest {

    @Test
    fun emailValidator_CorrectEmail_ReturnsTrue() {
        assertTrue(FieldsValidator.validateEmail("name@email.com").first)
    }

    @Test
    fun emailValidator_IncorrectEmail_ReturnsFalse() {
        assertFalse(FieldsValidator.validateEmail("incorrectEmail").first)
    }

    @Test
    fun phoneValidator_CorrectPhone_ReturnsTrue() {
        assertTrue(FieldsValidator.validatePhone("4155552671").first)
    }

    @Test
    fun phoneValidator_IncorrectPhone_ReturnsFalse() {
        assertFalse(FieldsValidator.validatePhone("38028284114421").first)
    }

    @Test
    fun passwordValidator_CorrectPass_ReturnsTrue() {
        assertTrue(FieldsValidator.validatePassword("Qwertyu123", "Qwertyu123").first)
    }

    @Test
    fun passwordValidator_PassDoesntMatch_ReturnsFalse() {
        assertFalse(FieldsValidator.validatePassword("Qwertyu123", "Qwertyu122").first)
    }

    @Test
    fun passwordValidator_PassLessThan8Characters_ReturnsFalse() {
        assertFalse(FieldsValidator.validatePassword("qwrr", "qwrr").first)
    }

    @Test
    fun passwordValidator_PassMoreThan26Characters_ReturnsFalse() {
        assertFalse(
            FieldsValidator.validatePassword(
                "Qwerty5125askfaklsfjklasfjlaasfasfzxvzxvzxvasfasfaswrqw",
                "Qwerty5125askfaklsfjklasfjlaasfasfzxvzxvzxvasfasfaswrqw"
            ).first
        )
    }

    @Test
    fun passwordValidator_ConfPassNotEntered_ReturnsFalse() {
        assertFalse(
            FieldsValidator.validatePassword(
                "Test6725Test",
                ""
            ).first
        )
    }

    @Test
    fun firstAndLastNameValidator_FieldsValid_ReturnsTrue() {
        assertTrue(
            FieldsValidator.validateFirstAndLastName(
                "James",
                "Bond"
            ).first
        )
    }

    @Test
    fun firstAndLastNameValidator_FieldsInvalid_ReturnsFalse() {
        assertFalse(
            FieldsValidator.validateFirstAndLastName(
                "",
                ""
            ).first
        )
    }
}
