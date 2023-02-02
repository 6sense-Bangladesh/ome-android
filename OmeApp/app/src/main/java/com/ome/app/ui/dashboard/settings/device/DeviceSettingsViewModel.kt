package com.ome.app.ui.dashboard.settings.device

import com.ome.app.base.BaseViewModel
import com.ome.app.data.remote.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DeviceSettingsViewModel @Inject constructor(val userRepository: UserRepository) : BaseViewModel() {


    fun loadSettings() {

    }

}

