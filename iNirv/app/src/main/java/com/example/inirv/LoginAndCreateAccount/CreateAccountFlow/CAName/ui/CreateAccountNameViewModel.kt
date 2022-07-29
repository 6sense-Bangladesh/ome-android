package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAName.ui

import androidx.lifecycle.ViewModel
import com.example.inirv.Interfaces.CoordinatorInteractor

class CreateAccountNameViewModel(
    override var onFinished: ((CoordinatorInteractor) -> Unit)?
) : ViewModel(), CoordinatorInteractor {

}