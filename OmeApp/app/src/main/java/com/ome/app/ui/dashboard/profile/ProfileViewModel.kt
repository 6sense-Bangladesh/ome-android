package com.ome.app.ui.dashboard.profile

import com.ome.app.base.BaseViewModel
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val amplifyManager: AmplifyManager,
    val preferencesProvider: PreferencesProvider
) : BaseViewModel() {

    fun signOut() {
        launch(dispatcher = ioContext) {
            amplifyManager.signUserOut()
            preferencesProvider.clearData()
            amplifyManager.signOutFlow.emit(true)
        }
    }
}
