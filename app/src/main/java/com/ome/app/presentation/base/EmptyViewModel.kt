package com.ome.app.presentation.base

import com.ome.app.data.local.PreferencesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class EmptyViewModel @Inject constructor(val pref : PreferencesProvider) : BaseViewModel()
