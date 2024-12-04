package com.ome.app

import com.google.common.truth.Truth.assertThat
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.signin.SignInViewModel
import org.junit.Before
import org.junit.Test

class SignInAndroidUnitTest {

    private lateinit var viewModel: SignInViewModel
    private lateinit var amplifyManager: AmplifyManager
    private lateinit var userRepository: UserRepository
    private lateinit var stoveRepository: StoveRepository
    private lateinit var pref: PreferencesProvider

    @Before
    fun setup(){
    }

    @Test
    fun signInWithEmptyUserName_ReturnsFalse() {
        viewModel.signIn("", "password")
        assertThat(viewModel.destinationAfterSignFlow).isNull()
    }
}