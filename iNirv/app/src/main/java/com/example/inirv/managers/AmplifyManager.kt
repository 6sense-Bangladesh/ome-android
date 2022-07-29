package com.example.inirv.managers



//import com.amplifyframework.auth.*
//import com.amplifyframework.auth.result.AuthResetPasswordResult
//import com.amplifyframework.auth.result.AuthSignInResult
//import com.amplifyframework.auth.result.AuthSignUpResult
//import com.amplifyframework.core.Amplify

import com.amplifyframework.auth.*
import com.amplifyframework.auth.options.AuthResendSignUpCodeOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.kotlin.auth.KotlinAuthFacade
import com.amplifyframework.kotlin.core.Amplify

class AmplifyResultValue{
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

object AmplifyManager {

    // TODO: Remove when done with amplify manager tests
    var accessToken: String = ""
    // TODO: EOR
//    var auth: AuthCategory = Amplify.Auth
    var kotAuth: KotlinAuthFacade = Amplify.Auth
    private var restManager: RESTManager = RESTManager

    suspend fun fetchAuthSession(completion: (AmplifyResultValue) -> Unit){

        val resultValue: AmplifyResultValue = AmplifyResultValue()
        try {
            val session = kotAuth.fetchAuthSession()
            resultValue.session = session
            resultValue.wasCallSuccessful = true

            completion(resultValue)

        } catch (error: AuthException) {

            resultValue.authException = error
            resultValue.wasCallSuccessful = false
            completion(resultValue)
        }
    }

    suspend fun fetchUserAttributes(completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        try {
            val attributes = kotAuth.fetchUserAttributes()

            resultValue.attributes = attributes
            resultValue.wasCallSuccessful = true
        } catch (error: AuthException){

            resultValue.wasCallSuccessful = false
            resultValue.authException = error
        }

        completion(resultValue)
    }

    suspend fun signUserOut(completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        try {
            kotAuth.signOut()

            resultValue.wasCallSuccessful = true
        } catch (error: AuthException){

            resultValue.authException = error
            resultValue.wasCallSuccessful = false
        }

        completion(resultValue)
    }

    suspend fun signUserIn(username: String,
                           password: String,
                           completion: (AmplifyResultValue) -> Unit){


        val resultValue = AmplifyResultValue()

        try {

            val signInResult = kotAuth.signIn(username, password)
            resultValue.wasCallSuccessful = true
            resultValue.signInResult = signInResult

            when(signInResult.nextStep.signInStep){
                AuthSignInStep.CONFIRM_SIGN_UP -> {
                    resultValue.message = "Confirm signup"
                }

                AuthSignInStep.DONE -> {
                    resultValue.message = "Done"
                }

                else -> {
                    resultValue.message = "Not handled case result.nextStep: ${signInResult.nextStep.signInStep}"
                }
            }
        } catch (error: AuthException){

            resultValue.wasCallSuccessful = false
            resultValue.authException = error
        }

        completion(resultValue)
    }

    suspend fun updatePassword(oldPassword: String,
                       newPassword: String,
                       completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        try{

            kotAuth.updatePassword(oldPassword, newPassword)

            resultValue.wasCallSuccessful = true
            resultValue.message = "Change password succeeded"
        } catch (error: AuthException){

            resultValue.authException = error
            resultValue.wasCallSuccessful = false
        }

        completion(resultValue)

    }

    suspend fun confirmResetPassword(password: String,
                                     confirmationCode: String,
                                     completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        try {

            kotAuth.confirmResetPassword(password, confirmationCode)

            resultValue.wasCallSuccessful = true
            resultValue.message = "Password reset confirmed"
        } catch (error: AuthException){

            resultValue.authException = error
            resultValue.wasCallSuccessful = false
        }

        completion(resultValue)
    }

    suspend fun signUp(email: String,
                       password: String,
                       phoneNumber: String = "",
                       completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        val signUpOptions = AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email)

        if (phoneNumber.isNotEmpty()){
            signUpOptions.userAttribute(AuthUserAttributeKey.phoneNumber(), phoneNumber)
        }

        try {
            val signUpResult = kotAuth.signUp(email, password, signUpOptions.build())

            resultValue.signUpResult = signUpResult
            resultValue.wasCallSuccessful = true
        } catch (error: AuthException){

            resultValue.wasCallSuccessful = false
            resultValue.authException = error
        }

        completion(resultValue)

    }

    suspend fun resendSignUpCode(email: String,
                                 completion: (AmplifyResultValue) -> Unit){

        val options = AuthResendSignUpCodeOptions.DefaultAuthResendSignUpCodeOptions.defaults()
        val resultValue = AmplifyResultValue()

        try {

            val resendSignUpCodeResult = kotAuth.resendSignUpCode(email, options)

            resultValue.wasCallSuccessful = true
            resultValue.deliveryDetails = resendSignUpCodeResult.nextStep.codeDeliveryDetails
        } catch (error: AuthException){

            resultValue.wasCallSuccessful = false
            resultValue.authException = error
        }

        completion(resultValue)

    }

    suspend fun confirmSignUp(email: String,
                              confirmationCode: String,
                              completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        try {

            val confirmSignUpResultValue = kotAuth.confirmSignUp(email, confirmationCode)

            resultValue.wasCallSuccessful = true
            resultValue.signUpResult = confirmSignUpResultValue

        } catch (error: AuthException){

            resultValue.wasCallSuccessful = false
            resultValue.authException = error
        }

        completion(resultValue)

    }

    suspend fun resetPassword(email: String, completion: (AmplifyResultValue) -> Unit){

        val resultValue = AmplifyResultValue()

        try {

            val resetPasswordResultValue = kotAuth.resetPassword(email)

            resultValue.wasCallSuccessful = true
            resultValue.authResetPasswordResult = resetPasswordResultValue

        } catch (error: AuthException){

            resultValue.wasCallSuccessful = false
            resultValue.authException = error
        }

        completion(resultValue)

    }
}