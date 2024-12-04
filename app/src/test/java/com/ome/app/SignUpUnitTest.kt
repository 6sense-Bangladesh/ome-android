package com.ome.app

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SignUpUnitTest {
    @Test
    fun `signup with empty username returns false`() {
        SignUpValidator.createUser("", "jhoxam@gmail.com", "", "Password12345", "Password12345")
            .apply { assertThat(this).isFalse() }
    }

    @Test
    fun `signup with empty password returns false`() {
        SignUpValidator.createUser("jhoxam", "jhoxam@gmail.com", "", "", "")
            .apply { assertThat(this).isFalse() }
    }

    @Test
    fun `signup with empty email returns false`() {
        SignUpValidator.createUser("jhoxam", "", "", "Password12345", "Password12345")
            .apply { assertThat(this).isFalse() }
    }

    @Test
    fun `signup with wrong confirm password returns false`() {
        SignUpValidator.createUser("jhoxam", "jhoxam@gmail.com", "", "Password12345", "Password123")
            .apply { assertThat(this).isFalse() }
    }

    @Test
    fun `signup with less than 9 characters password returns false`() {
        SignUpValidator.createUser("jhoxam", "jhoxam@gmail.com", "", "Password", "Password")
            .apply { assertThat(this).isFalse() }
    }

    @Test
    fun `signup with valid US phone returns true`() {
        SignUpValidator.createUser(
            "jhoxam",
            "jhoxam@gmail.com",
            "15552345678",
            "Password12345",
            "Password12345"
        ).apply {
            assertThat(this).isTrue()
        }
    }

    @Test
    fun `signup with invalid US phone returns false`() {
        SignUpValidator.createUser(
            "jhoxam",
            "jhoxam@gmail.com",
            "35552345678",
            "Password12345",
            "Password12345"
        ).apply {
            assertThat(this).isFalse()
        }
    }

    @Test
    fun `signup with existing email returns false`() {
        SignUpValidator.createUser(
            "jhoxam",
            "john.mclean@examplepetstore.com",
            "",
            "Password12345",
            "Password12345"
        ).apply {
            assertThat(this).isFalse()
        }
    }
}