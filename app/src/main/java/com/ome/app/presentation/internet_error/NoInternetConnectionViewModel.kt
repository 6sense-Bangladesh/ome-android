package com.ome.app.presentation.internet_error

import com.ome.app.data.ConnectionStatusListener
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NoInternetConnectionViewModel @Inject constructor(
    val connectionStatusListener: ConnectionStatusListener
): BaseViewModel()
