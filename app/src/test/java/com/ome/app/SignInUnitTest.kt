package com.ome.app

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SignInUnitTest {
    @Test
    fun signInWithFalseEmail_ReturnsFalse() {
        SignInValidator.validateSignIn("abc@gmail.c", "123456").apply {
            assertThat(this).isFalse()
        }
    }

    @Test
    fun signInWithFalsePassword_ReturnsFalse() {
        SignInValidator.validateSignIn("abc@gmail.com", "12").apply {
            assertThat(this).isFalse()
        }
    }

    @Test
    fun signInWithCorrectEmailPassword_ReturnsTrue() {
        SignInValidator.validateSignIn("clerknight@gmail.com", "123456").apply {
            assertThat(this).isTrue()
        }
    }

    @Test
    fun signInWithBlankEmail() {
        SignInValidator.validateSignIn(" ", "123456").apply {
            assertThat(this).isFalse()
        }
    }

    @Test
    fun signInWithBlankPassword() {
        SignInValidator.validateSignIn("def@gmail.com", " ").apply {
            assertThat(this).isFalse()
        }
    }
}