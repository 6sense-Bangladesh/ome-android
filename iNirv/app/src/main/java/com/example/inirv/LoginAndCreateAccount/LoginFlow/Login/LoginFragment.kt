package com.example.inirv.LoginAndCreateAccount.LoginFlow.Login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.inirv.AppLevelNavGraphDirections
import com.example.inirv.R

enum class LoginFragmentButton{
    login,
    forgotPassword
}

interface LoginFragmentDelegate{

    fun loginFragmentButtonPressed(
        whichButton: LoginFragmentButton,
        userName: String = "",
        password:String = ""
    )

    fun onStart()
}

class LoginFragment(
): Fragment() {

    val viewModel: ViewModel
    val delegate: LoginFragmentDelegate
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null

    companion object {
        fun newInstance() = LoginFragment()
    }

    init {
        viewModel = LoginViewModel(null)
        delegate = viewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()

        delegate.onStart()

        setup()
    }

    fun setup(){

        emailEditText = this.view?.findViewById<EditText>(
            R.id.login_email_edit_text
        )

        passwordEditText = this.view?.findViewById<EditText>(
            R.id.login_password_edit_text
        )

        val continueBtn = this.view?.findViewById<Button>(
            R.id.login_continue_button
        )

        val forgotPasswordBtn = this.view?.findViewById<Button>(
            R.id.login_forgot_password_button
        )

        continueBtn?.setOnClickListener {
            delegate.loginFragmentButtonPressed(
                LoginFragmentButton.login,
                emailEditText?.text.toString(),
                passwordEditText?.text.toString()
            )
        }

        forgotPasswordBtn?.setOnClickListener {
            delegate.loginFragmentButtonPressed(
                LoginFragmentButton.forgotPassword,
                "",
                ""
            )
        }

        if (viewModel is LoginViewModel){
            viewModel.errorMessageLiveData.observe(
                this, Observer { errorMessage ->

                    val alertDialogBuilder = AlertDialog.Builder(this.context)

                    alertDialogBuilder.setTitle("Warning")
                    alertDialogBuilder.setMessage(errorMessage)
                    alertDialogBuilder.setNegativeButton("Dismiss", null)

                    alertDialogBuilder.show()
                })

            val screensObserver = Observer<LoginGoToScreens>{ screen ->

                val action = when(screen){
                    LoginGoToScreens.parentNavigator -> AppLevelNavGraphDirections.actionGlobalLaunchFragment()
                    LoginGoToScreens.forgotPassword ->  LoginFragmentDirections.actionLoginFragmentToForgotFragment()
                    LoginGoToScreens.caConfirm -> LoginFragmentDirections.actionLoginFragmentToCaConfirmFragment()
                    else -> return@Observer
                }

                activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)
            }

            viewModel.loginScreens.observe(this, screensObserver)
        }

    }
}