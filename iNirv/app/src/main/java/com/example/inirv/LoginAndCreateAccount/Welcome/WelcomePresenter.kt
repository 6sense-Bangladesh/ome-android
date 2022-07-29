package com.example.inirv.LoginAndCreateAccount.Welcome

import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.Interfaces.Presenter

interface WelcomePresenterDelegate{

    fun showUpdateAlertView(versionNumber: String)
}

class WelcomePresenter(
    override var onFinished: ((CoordinatorInteractor) -> Unit)?,
    var _loginBtnPressed: Boolean = false,
    var _btnPressed: Boolean = false,
    var _delegate: WelcomePresenterDelegate? = null
): Presenter, WelcomeFragmentDelegate {

    var loginBtnPressed: Boolean = _loginBtnPressed
        private set
    var btnPressed: Boolean = _btnPressed
        private set
    var delegate: WelcomePresenterDelegate? = _delegate
        private set

    override fun setup() {
        // Reset action variables
        loginBtnPressed = false
        btnPressed = false
    }

    /** TODO(Need to implement this)
     * A way to check for app updates when opening
     * the application and coming to the welcome screen
    */
    fun checkForUpdates(){

    }

    // MARK:  WelcomeFragmentDelegate
    override fun buttonPressed(isLoginButton: Boolean) {

        if (btnPressed){
            return
        }

        btnPressed = true
        loginBtnPressed = isLoginButton

        this.onFinished?.let { it(this) }
    }

}