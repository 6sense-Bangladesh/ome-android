package com.ome.app.ui.base

import com.ome.app.utils.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class EmptyViewModel @Inject constructor(val socketManager: SocketManager) : BaseViewModel()
