package com.example.inirv.LoginAndCreateAccount.Welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.inirv.R

interface WelcomeFragmentDelegate{
    fun buttonPressed(isLoginButton: Boolean)
}

class WelcomeFragment(
//    _viewModel: ViewModel,
//    _delegate: WelcomeFragmentDelegate? = null
): Fragment() {

    val viewModel: ViewModel
    lateinit var coordinator: WelcomeCoordinator
        private set
    lateinit var navigator: WelcomeNavigator
        private set
    val delegate: WelcomeFragmentDelegate

    init {

        viewModel = WelcomeViewModel(null)
        delegate = viewModel
    }

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_welcome, container, false) //.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        setup()
    }

    fun setup(){

        val loginButton = this.view?.findViewById<Button>(
            R.id.welcome_fragment_login_button
        )
        loginButton?.setOnClickListener {
            delegate.buttonPressed(true)
        }

        val createAccountButton = this.view?.findViewById<Button>(
            R.id.welcome_fragment_create_account_button
        )
        createAccountButton?.setOnClickListener {
            delegate.buttonPressed(false)
        }

        if (viewModel is WelcomeViewModel){

            val loginButtonObserver = Observer<Boolean>{ isLoginButton ->

                // Determine what fragment to go to

                val action = if (isLoginButton){
                    WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
                } else {
                    WelcomeFragmentDirections.actionWelcomeFragmentToCreateAccountNameFragment()
                }

                activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)

            }

            viewModel.loginButtonPressed.observe(this, loginButtonObserver)
        }
    }
}