package com.example.inirv.StoveTabTests.Profile


import android.content.SharedPreferences
import com.example.inirv.Home.Profile.ProfileGoToScreens
import com.example.inirv.Home.Profile.ProfileViewModel
import com.example.inirv.managers.AmplifyManager
import com.example.inirv.managers.AmplifyResultValue
import com.example.inirv.managers.KnobManager
import com.example.inirv.managers.UserManager
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ProfileVMTests {

    @MockK
    lateinit var mock_userManager: UserManager

    @MockK
    lateinit var mock_knobManager: KnobManager

    @MockK
    lateinit var mock_sharedPreferences: SharedPreferences

    @MockK
    lateinit var mock_amplifyManager: AmplifyManager

    @MockK
    lateinit var mock_websocketManager: WebsocketManager

    lateinit var profileVM: ProfileViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)

        profileVM = ProfileViewModel(
            mock_sharedPreferences,
            mock_knobManager,
            mock_userManager,
            mock_amplifyManager,
            mock_websocketManager
        )

    }

    @Test
    fun test_updateDevTokens(){

        val deviceToken = "TestDeviceTokenToRemove"
        every {
            mock_sharedPreferences.getString("omePreferences", "deviceToken")
        } returns deviceToken

        every {
            mock_userManager.user?.deviceTokens
        } returns listOf(deviceToken, "TestDeviceToken1","TestDeviceToken2")

        profileVM.updateDevTokens()

        val params = mapOf(Pair("deviceTokens", "TestDeviceToken1,TestDeviceToken2"))
        verify {
            mock_userManager.updateUserProfile(params)
        }
    }

    @Test
    fun test_saveSharedPreferences(){

        val testDeviceToken = "TestDeviceToken"
        val testConnectedToWifi = "TestConnectedToWifi"
        val testPublicIPAddress = "TestPublicIPAddress"
        val testNextTime = false
        val testVersionToSkip = "TestVersionToSkip"
        val testNames = "TestNames"

        every {
            mock_sharedPreferences.getString("deviceToken", "")
        } returns testDeviceToken

        every {
            mock_sharedPreferences.getString("connectedToWifi", "")
        } returns testConnectedToWifi

        every {
            mock_sharedPreferences.getString("publicIPAddress", "")
        } returns testPublicIPAddress

        every {
            mock_sharedPreferences.getBoolean("nextTime", false)
        } returns testNextTime

        every {
            mock_sharedPreferences.getString("versionToSkip", "")
        } returns testVersionToSkip

        every {
            mock_sharedPreferences.getString("names", "")
        } returns testNames

        // Tested function
        profileVM.saveSharedPreferences()

        verify {
            // Call to clear shared preferences
            mock_sharedPreferences.edit().clear()

            // Added all of the necessary shared preferences back
            mock_sharedPreferences.edit().putString("deviceToken", testDeviceToken)
            mock_sharedPreferences.edit().putString("connectedToWifi", testConnectedToWifi)
            mock_sharedPreferences.edit().putString("publicIPAddress", testPublicIPAddress)
            mock_sharedPreferences.edit().putBoolean("nextTime", testNextTime)
            mock_sharedPreferences.edit().putString("versionToSkip", testVersionToSkip)
            mock_sharedPreferences.edit().putString("names", testNames)

            // Commit the changes
            mock_sharedPreferences.edit().commit()

        }

    }

    @Test
    fun test_logoutUserHandlerSuccess(){

        // Tested function call
        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = true
        profileVM.logoutUserHandler(amplifyResultValue)

        verify {
            mock_userManager.removeDelegate(profileVM)
            mock_knobManager.removeAllKnobs()
            mock_websocketManager.disconnectFromWebsocket()
        }

        assert(profileVM.goToScreens.value != null)
        assert(profileVM.goToScreens.value == ProfileGoToScreens.logout)
    }

    @Test
    fun test_updateUserProfile(){

        // Tested function call
        val firstName: String = "TestFirst"
        val lastName: String = "TestLast"
        profileVM.updateUserProfile(firstName, lastName)

        val params = mapOf(Pair("firstName", firstName), Pair("lastName", lastName))
        verify {
            mock_userManager.updateUserProfile(params)
        }
    }
}