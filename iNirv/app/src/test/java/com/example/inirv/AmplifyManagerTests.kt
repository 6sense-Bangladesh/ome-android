package com.example.inirv

import com.amplifyframework.auth.*
import com.amplifyframework.auth.options.AuthResendSignUpCodeOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.*
import com.amplifyframework.kotlin.auth.KotlinAuthFacade
import com.example.inirv.managers.AmplifyManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class AmplifyManagerTests {

    @MockK
    lateinit var mockKotAuth: KotlinAuthFacade// = Amplify.Auth

    lateinit var amplifyManager: AmplifyManager

    @Before
    fun setup(){

        MockKAnnotations.init(this)

        amplifyManager = AmplifyManager
    }

    @After
    fun tearDown(){

    }

    @Test
    fun test_fetchAuthSessionSuccess() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth
        val toReturn = AuthSession(true)
        coEvery { mockKotAuth.fetchAuthSession() } returns toReturn

        amplifyManager.fetchAuthSession { resultValue ->

            coVerify { mockKotAuth.fetchAuthSession() }

            assert(resultValue.wasCallSuccessful)
            assert(resultValue.session === toReturn)
            assert(resultValue.authException == null)
        }
    }

    @Test
    fun test_fetchAuthSessionFailure() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth
        var fetchAuthSessionReturnValue = AuthException("Test Error", "Test Recovery Suggestion")

        coEvery { mockKotAuth.fetchAuthSession() } throws fetchAuthSessionReturnValue

        amplifyManager.fetchAuthSession { amplifyResultValue ->

            coVerify { mockKotAuth.fetchAuthSession() }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException === fetchAuthSessionReturnValue)
            assert(amplifyResultValue.authException?.message == "Test Error")
            assert(amplifyResultValue.session == null)
        }
    }

    @Test
    fun test_fetchUserAttributesSuccess() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        var fetchUserAttributesReturnValue = listOf(
            AuthUserAttribute(AuthUserAttributeKey.email(), "Test Email"),
            AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), "Test Phone Number")
        )

        coEvery { mockKotAuth.fetchUserAttributes() } returns fetchUserAttributesReturnValue

        amplifyManager.fetchUserAttributes { amplifyResultValue ->

            coVerify { mockKotAuth.fetchUserAttributes() }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.attributes === fetchUserAttributesReturnValue)
            assert(amplifyResultValue.attributes?.size == 2 )
            assert(amplifyResultValue.attributes?.get(0) == AuthUserAttribute(AuthUserAttributeKey.email(), "Test Email"))
            assert(amplifyResultValue.attributes?.get(1) == AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), "Test Phone Number"))
            assert(amplifyResultValue.authException == null)
        }

    }

    @Test
    fun test_fetchUserAttributesFailure() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        var fetchUserAttributesReturnValue = AuthException("Test Error", "Test recovery suggestion")

        coEvery { mockKotAuth.fetchUserAttributes() } throws fetchUserAttributesReturnValue

        amplifyManager.fetchUserAttributes { amplifyResultValue ->

            coVerify { mockKotAuth.fetchUserAttributes() }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException === fetchUserAttributesReturnValue)
            assert(amplifyResultValue.authException?.message == "Test Error")
            assert(amplifyResultValue.attributes == null)
        }
    }

    @Test
    fun test_signUserOutSuccess() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        coEvery { mockKotAuth.signOut() } returns Unit

        amplifyManager.signUserOut(){ amplifyResultValue ->

            coVerify { mockKotAuth.signOut() }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException == null)
        }
    }

    @Test
    fun test_signUserOutFailure() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        var signUserOutReturnValue = AuthException("Test Error", "Test recovery suggestion")

        coEvery { mockKotAuth.signOut() } throws signUserOutReturnValue

        amplifyManager.signUserOut() { amplifyResultValue ->

            coVerify { mockKotAuth.signOut() }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException === signUserOutReturnValue)
            assert(amplifyResultValue.authException?.message == "Test Error")
        }
    }

    @Test
    fun test_signUserInConfirmSignupSuccess() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        var signUserInResultValue = AuthSignInResult(true,
            AuthNextSignInStep(AuthSignInStep.CONFIRM_SIGN_UP,
                mapOf(),
                AuthCodeDeliveryDetails("Test",
                    AuthCodeDeliveryDetails.DeliveryMedium.EMAIL)
            )
        )

        val testUserName = "Test UserName"
        val testPassword = "Test Password"
        coEvery { mockKotAuth.signIn(testUserName, testPassword) } returns signUserInResultValue

        amplifyManager.signUserIn(testUserName, testPassword){ amplifyResultValue ->
            coVerify { mockKotAuth.signIn(username = testUserName, password = testPassword) }
            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signInResult === signUserInResultValue)
            assert(amplifyResultValue.authException == null)
            assert(amplifyResultValue.message == "Confirm signup")
        }
    }

    @Test
    fun test_signUserInDoneSuccess() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        var signUserInResultValue = AuthSignInResult(true,
            AuthNextSignInStep(AuthSignInStep.DONE,
                mapOf(),
                AuthCodeDeliveryDetails("Test",
                    AuthCodeDeliveryDetails.DeliveryMedium.EMAIL)
            )
        )

        val testUserName = "Test UserName"
        val testPassword = "Test Password"
        coEvery { mockKotAuth.signIn(testUserName, testPassword) } returns signUserInResultValue

        amplifyManager.signUserIn(testUserName, testPassword){ amplifyResultValue ->
            coVerify { mockKotAuth.signIn(username = testUserName, password = testPassword) }
            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signInResult === signUserInResultValue)
            assert(amplifyResultValue.authException == null)
            assert(amplifyResultValue.message == "Done")
        }
    }

    @Test
    fun test_signUserInDefaultSuccess() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        var signUserInResultValue = AuthSignInResult(true,
            AuthNextSignInStep(AuthSignInStep.RESET_PASSWORD,
                mapOf(),
                AuthCodeDeliveryDetails("Test",
                    AuthCodeDeliveryDetails.DeliveryMedium.EMAIL)
            )
        )

        val testUserName = "Test UserName"
        val testPassword = "Test Password"
        coEvery { mockKotAuth.signIn(testUserName, testPassword) } returns signUserInResultValue

        amplifyManager.signUserIn(testUserName, testPassword){ amplifyResultValue ->
            coVerify { mockKotAuth.signIn(username = testUserName, password = testPassword) }
            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signInResult === signUserInResultValue)
            assert(amplifyResultValue.authException == null)
            assert(amplifyResultValue.message == "Not handled case result.nextStep: RESET_PASSWORD")
        }
    }

    @Test
    fun test_signUserInFailure() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        var signUserInResultValue = AuthException("Test Error", "Test recovery suggestion")

        val testUserName = "Test UserName"
        val testPassword = "Test Password"
        coEvery { mockKotAuth.signIn(testUserName, testPassword) } throws signUserInResultValue

        amplifyManager.signUserIn(testUserName, testPassword){ amplifyResultValue ->
            coVerify { mockKotAuth.signIn(username = testUserName, password = testPassword) }
            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException === signUserInResultValue)
            assert(amplifyResultValue.signInResult == null)
            assert(amplifyResultValue.authException?.message == "Test Error")
        }
    }

    @Test
    fun test_updatePasswordSuccess() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        val testOldPassword = "Test Old Password"
        val testNewPassword = "Test New Password"

        coEvery { mockKotAuth.updatePassword(testOldPassword, testNewPassword) } returns Unit

        amplifyManager.updatePassword(testOldPassword, testNewPassword) { amplifyResultValue ->

            coVerify { mockKotAuth.updatePassword(oldPassword = testOldPassword, newPassword = testNewPassword) }
            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.message == "Change password succeeded")
        }
    }

    @Test
    fun test_updatePasswordFailure() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        val testOldPassword = "Test Old Password"
        val testNewPassword = "Test New Password"

        val updatePasswordResultValue = AuthException("Test Error", "Test recovery suggestion")

        coEvery { mockKotAuth.updatePassword(testOldPassword, testNewPassword) } throws updatePasswordResultValue

        amplifyManager.updatePassword(testOldPassword, testNewPassword) { amplifyResultValue ->

            coVerify { mockKotAuth.updatePassword(oldPassword = testOldPassword, newPassword = testNewPassword) }
            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException === updatePasswordResultValue)
        }
    }

    @Test
    fun test_confirmResetPasswordSuccess() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        val testEmail = "Test Email"
        val testPassword = "Test Password"
        val testConfirmationCode = "Test Confirmation Code"

        coEvery { mockKotAuth.confirmResetPassword(testPassword, testConfirmationCode) } returns Unit

        amplifyManager.confirmResetPassword(testPassword, testConfirmationCode){ amplifyResultValue ->

            coVerify { mockKotAuth.confirmResetPassword(newPassword = testPassword, confirmationCode = testConfirmationCode) }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.message == "Password reset confirmed")
        }
    }

    @Test
    fun test_confirmResetPassword_Failure() = runBlocking {

        amplifyManager.kotAuth = mockKotAuth

        val testEmail = "Test Email"
        val testPassword = "Test Password"
        val testConfirmationCode = "Test Confirmation Code"

        val confirmResetPasswordResultValue = AuthException("Test Error", "Test recovery suggestion")

        coEvery { mockKotAuth.confirmResetPassword(testPassword, testConfirmationCode) } throws confirmResetPasswordResultValue

        amplifyManager.confirmResetPassword(testPassword, testConfirmationCode){ amplifyResultValue ->

            coVerify { mockKotAuth.confirmResetPassword(newPassword = testPassword, confirmationCode = testConfirmationCode) }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException === confirmResetPasswordResultValue)
        }
    }

    @Test
    fun test_signUp_Success() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testUsername = "Test Username"
        val testPassword = "Test Password"
        val testPhoneNumber = "Test Phone Number"
        val testUserAttributes = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.phoneNumber(), testPhoneNumber)
            .userAttribute(AuthUserAttributeKey.email(), testUsername)
            .build()

        val signUpResultValue = AuthSignUpResult(true,
            AuthNextSignUpStep(AuthSignUpStep.DONE, mapOf(), null),
            AuthUser("Test ID", "Test Username")
        )

        coEvery { mockKotAuth.signUp(testUsername, testPassword, testUserAttributes) } returns signUpResultValue

        amplifyManager.signUp(testUsername, testPassword){ amplifyResultValue ->

            coVerify { mockKotAuth.signUp(
                username = testUsername,
                password = testPassword,
                options = testUserAttributes)
            }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signUpResult === signUpResultValue)
        }
    }

    @Test
    fun test_signUp_Fail() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testUsername = "Test Username"
        val testPassword = "Test Password"
        val testPhoneNumber = "Test Phone Number"
        val testUserAttributes = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.phoneNumber(), testPhoneNumber)
            .userAttribute(AuthUserAttributeKey.email(), testUsername)
            .build()

        val signUpResultValue = AuthException("Test Error", "Test Recovery Suggestion")

        coEvery { mockKotAuth.signUp(testUsername, testPassword, testUserAttributes) } throws signUpResultValue

        amplifyManager.signUp(testUsername, testPassword){ amplifyResultValue ->

            coVerify { mockKotAuth.signUp(
                username = testUsername,
                password = testPassword,
                options = testUserAttributes)
            }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signUpResult == null)
            assert(amplifyResultValue.authException === signUpResultValue)
        }
    }

    @Test
    fun test_resendSignUpCode_Success() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testEmail = "Test Email"
        val testOptions = AuthResendSignUpCodeOptions.defaults()

        val resendSignUpCodeReturnValue = AuthSignUpResult(true,
            AuthNextSignUpStep(AuthSignUpStep.DONE,
                mapOf(),
                AuthCodeDeliveryDetails("Test Destination", AuthCodeDeliveryDetails.DeliveryMedium.EMAIL) ),
            AuthUser("Test ID", "Test Username"))

        coEvery { mockKotAuth.resendSignUpCode(
            username = testEmail,
            options = testOptions)
        } returns resendSignUpCodeReturnValue

        amplifyManager.resendSignUpCode(testEmail){ amplifyResultValue ->

            coVerify { mockKotAuth.resendSignUpCode(
                username = testEmail,
                options = testOptions)
            }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.deliveryDetails ==
                    AuthCodeDeliveryDetails("Test Destination", AuthCodeDeliveryDetails.DeliveryMedium.EMAIL))
            assert(amplifyResultValue.authException == null)
        }

    }

    @Test
    fun test_resendSignUpCode_Failure() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testEmail = "Test Email"
        val testOptions = AuthResendSignUpCodeOptions.defaults()

        val resendSignUpCodeReturnValue = AuthException("Test Error", "Test Recovery Suggestion")

        coEvery { mockKotAuth.resendSignUpCode(
            username = testEmail,
            options = testOptions)
        } throws resendSignUpCodeReturnValue

        amplifyManager.resendSignUpCode(testEmail){ amplifyResultValue ->

            coVerify { mockKotAuth.resendSignUpCode(
                username = testEmail,
                options = testOptions)
            }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authException == resendSignUpCodeReturnValue)
            assert(amplifyResultValue.deliveryDetails == null)
        }

    }

    @Test
    fun test_confirmSignUp_Success() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testUsername = "Test Username"
        val testConfirmationCode = "Test Confirmation Code"

        val confirmSignUpResultValue = AuthSignUpResult(
            true,
            AuthNextSignUpStep(
                AuthSignUpStep.DONE,
                mapOf(),
                null),
            AuthUser("Test UserID", "Test UserName")
        )

        coEvery { mockKotAuth.confirmSignUp(
            username = testUsername,
            confirmationCode = testConfirmationCode)
        } returns confirmSignUpResultValue

        amplifyManager.confirmSignUp(testUsername, testConfirmationCode){ amplifyResultValue ->

            coVerify { mockKotAuth.confirmSignUp(
                username = testUsername,
                confirmationCode = testConfirmationCode)
            }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signUpResult == confirmSignUpResultValue)
            assert(amplifyResultValue.authException == null)
        }
    }

    @Test
    fun test_confirmSignUp_Failure() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testUsername = "Test Username"
        val testConfirmationCode = "Test Confirmation Code"

        val confirmSignUpResultValue = AuthException("Test Error", "Test Recovery Suggestion")

        coEvery { mockKotAuth.confirmSignUp(
            username = testUsername,
            confirmationCode = testConfirmationCode)
        } throws confirmSignUpResultValue

        amplifyManager.confirmSignUp(testUsername, testConfirmationCode){ amplifyResultValue ->

            coVerify { mockKotAuth.confirmSignUp(
                username = testUsername,
                confirmationCode = testConfirmationCode)
            }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.signUpResult == null)
            assert(amplifyResultValue.authException == confirmSignUpResultValue)
        }
    }

    @Test
    fun test_resetPassword_Success() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testUsername = "Test Username"

        val resetPasswordReturnValue = AuthResetPasswordResult(
            true,
            AuthNextResetPasswordStep(
                AuthResetPasswordStep.DONE,
                mapOf(),
                null)
        )

        coEvery { mockKotAuth.resetPassword(
            username = testUsername)
        } returns resetPasswordReturnValue

        amplifyManager.resetPassword(testUsername){ amplifyResultValue ->

            coVerify { mockKotAuth.resetPassword(
                username = testUsername)
            }

            assert(amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authResetPasswordResult == resetPasswordReturnValue)
            assert(amplifyResultValue.authException == null)
        }
    }

    @Test
    fun test_resetPassword_Failure() = runBlocking{

        amplifyManager.kotAuth = mockKotAuth

        val testUsername = "Test Username"

        val resetPasswordReturnValue = AuthException("Test Error", "Test Recovery Suggestion")

        coEvery { mockKotAuth.resetPassword(
            username = testUsername)
        } throws resetPasswordReturnValue

        amplifyManager.resetPassword(testUsername){ amplifyResultValue ->

            coVerify { mockKotAuth.resetPassword(
                username = testUsername)
            }

            assert(!amplifyResultValue.wasCallSuccessful)
            assert(amplifyResultValue.authResetPasswordResult == null )
            assert(amplifyResultValue.authException == resetPasswordReturnValue)
        }
    }
}