package com.ome.app.presentation.signup.welcome

import com.ome.app.data.local.PreferencesProvider
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(val pref: PreferencesProvider) : BaseViewModel()
