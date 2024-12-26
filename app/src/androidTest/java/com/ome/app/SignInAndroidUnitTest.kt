package com.ome.app

import com.google.common.truth.Truth.assertThat
import com.ome.app.presentation.signin.SignInViewModel
import org.junit.Test

class SignInAndroidUnitTest {

    private lateinit var viewModel: SignInViewModel

    @Test
    fun signInWithEmptyUserName_ReturnsFalse() {
        viewModel.signIn("", "password")
        assertThat(viewModel.destinationAfterSignFlow).isNull()
    }
}