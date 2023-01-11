package com.ome.app.data.remote

import com.amplifyframework.auth.*
import com.amplifyframework.auth.options.AuthResendSignUpCodeOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.kotlin.auth.KotlinAuthFacade
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class AmplifyResultValue {
    var attributes: List<AuthUserAttribute>? = null
    var session: AuthSession? = null
    var signUpResult: AuthSignUpResult? = null
    var signInResult: AuthSignInResult? = null
    var authException: AuthException? = null
    var message: String? = null
    var wasCallSuccessful: Boolean = false
    var deliveryDetails: AuthCodeDeliveryDetails? = null
    var authResetPasswordResult: AuthResetPasswordResult? = null
}

class AmplifyManager {

    // TODO: Remove when done with amplify manager tests
    var accessToken: String = ""

    // TODO: EOR
//    var auth: AuthCategory = Amplify.Auth
    var kotAuth: KotlinAuthFacade = Amplify.Auth
//    private var restManager: RESTManager = RESTManager

    val signOutFlow: MutableSharedFlow<Boolean> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    suspend fun fetchAuthSession(): AmplifyResultValue {
        val resultValue: AmplifyResultValue = AmplifyResultValue()
        return try {
            val session = kotAuth.fetchAuthSession()
            resultValue.session = session
            resultValue.wasCallSuccessful = true
            resultValue
        } catch (error: AuthException) {
            resultValue.authException = error
            resultValue.wasCallSuccessful = false
            resultValue
        }
    }

    suspend fun fetchUserAttributes(): AmplifyResultValue {
        val resultValue = AmplifyResultValue()
        val attributes = kotAuth.fetchUserAttributes()
        resultValue.attributes = attributes

        return resultValue
    }

    suspend fun signUserOut() = kotAuth.signOut()


    suspend fun signUserIn(
        username: String,
        password: String
    ): AmplifyResultValue {

        val resultValue = AmplifyResultValue()

        val signInResult = kotAuth.signIn(username, password)
        resultValue.signInResult = signInResult

        when (signInResult.nextStep.signInStep) {
            AuthSignInStep.CONFIRM_SIGN_UP -> {
                resultValue.message = "Confirm signup"
            }

            AuthSignInStep.DONE -> {
                resultValue.message = "Done"
            }

            else -> {
                resultValue.message =
                    "Not handled case result.nextStep: ${signInResult.nextStep.signInStep}"
            }
        }

        return resultValue
    }


    suspend fun deleteUser(): AmplifyResultValue {
        val resultValue = AmplifyResultValue()

        try {
            kotAuth.deleteUser()

            resultValue.wasCallSuccessful = true
        } catch (error: AuthException) {

            resultValue.authException = error
            resultValue.wasCallSuccessful = false
        }

        return resultValue
    }

    suspend fun updatePassword(
        oldPassword: String,
        newPassword: String,
    ): AmplifyResultValue {
        val resultValue = AmplifyResultValue()
        kotAuth.updatePassword(oldPassword, newPassword)
        resultValue.message = "Change password succeeded"

        return resultValue
    }

    suspend fun confirmResetPassword(
        password: String,
        confirmationCode: String
    ): AmplifyResultValue {

        val resultValue = AmplifyResultValue()

        kotAuth.confirmResetPassword(password, confirmationCode)

        resultValue.wasCallSuccessful = true
        resultValue.message = "Password reset confirmed"

        return resultValue
    }

    suspend fun signUp(
        email: String,
        password: String,
        phoneNumber: String = ""
    ): AmplifyResultValue {

        val resultValue = AmplifyResultValue()

        val signUpOptions =
            AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email)

        if (phoneNumber.isNotEmpty()) {
            signUpOptions.userAttribute(AuthUserAttributeKey.phoneNumber(), phoneNumber)
        }

        val signUpResult = kotAuth.signUp(email, password, signUpOptions.build())

        resultValue.signUpResult = signUpResult
        resultValue.wasCallSuccessful = true

        return resultValue
    }

    suspend fun resendSignUpCode(
        email: String
    ): AmplifyResultValue {

        val options = AuthResendSignUpCodeOptions.DefaultAuthResendSignUpCodeOptions.defaults()
        val resultValue = AmplifyResultValue()

        val resendSignUpCodeResult = kotAuth.resendSignUpCode(email, options)

        resultValue.wasCallSuccessful = true
        resultValue.deliveryDetails = resendSignUpCodeResult.nextStep.codeDeliveryDetails


        return resultValue

    }

    suspend fun confirmSignUp(
        email: String,
        confirmationCode: String,
    ): AmplifyResultValue {

        val resultValue = AmplifyResultValue()

        val confirmSignUpResultValue = kotAuth.confirmSignUp(email, confirmationCode)

        resultValue.wasCallSuccessful = true
        resultValue.signUpResult = confirmSignUpResultValue

        return resultValue
    }

    suspend fun resetPassword(email: String): AmplifyResultValue {

        val resultValue = AmplifyResultValue()

        val resetPasswordResultValue = kotAuth.resetPassword(email)

        resultValue.wasCallSuccessful = true
        resultValue.authResetPasswordResult = resetPasswordResultValue

        return resultValue

    }
}
