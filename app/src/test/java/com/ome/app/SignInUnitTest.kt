package com.ome.app

import com.google.common.truth.Truth.assertThat
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.signin.SignInViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SignInUnitTest {
    private lateinit var viewModel: SignInViewModel
    private lateinit var amplifyManager: AmplifyManager
    private lateinit var userRepository: UserRepository
    private lateinit var stoveRepository: StoveRepository
    private lateinit var preferencesProvider: PreferencesProvider

    @Before
    fun setup() {
        amplifyManager = Mockito.mock(AmplifyManager::class.java)
        userRepository = Mockito.mock(UserRepository::class.java)
        stoveRepository = Mockito.mock(StoveRepository::class.java)
        preferencesProvider = Mockito.mock(PreferencesProvider::class.java)
        viewModel = SignInViewModel(
            amplifyManager,
            preferencesProvider,
            userRepository,
            stoveRepository
        )
    }

    @Test
    fun signInWithFalseEmail_ReturnsFalse() {
        val expectedData = viewModel.signIn("", "12345")
        Mockito.`when`(viewModel.signIn("", "12345")).thenReturn(expectedData)

        viewModel.fetchUserData()

        assertThat(viewModel.signInStatus.value).isFalse()
    }
}