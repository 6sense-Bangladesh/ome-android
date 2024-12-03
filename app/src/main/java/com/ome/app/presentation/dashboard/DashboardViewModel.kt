package com.ome.app.presentation.dashboard

import com.ome.app.data.local.PreferencesProvider
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(val pref: PreferencesProvider) : BaseViewModel()
