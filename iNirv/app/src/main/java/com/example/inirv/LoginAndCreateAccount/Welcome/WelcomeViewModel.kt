package com.example.inirv.LoginAndCreateAccount.Welcome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inirv.Interfaces.CoordinatorInteractor

class WelcomeViewModel(
    override var onFinished: ((CoordinatorInteractor) -> Unit)?
): ViewModel(), CoordinatorInteractor, WelcomeFragmentDelegate {

    var loginButtonPressed: MutableLiveData<Boolean>
        private set

    init {
        loginButtonPressed = MutableLiveData()
    }

    override fun buttonPressed(isLoginButton: Boolean) {

        loginButtonPressed.value = isLoginButton

//        onFinished?.invoke(this)
    }
}