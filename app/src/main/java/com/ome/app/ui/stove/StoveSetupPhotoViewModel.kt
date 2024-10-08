package com.ome.app.ui.stove

import com.ome.app.data.remote.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class StoveSetupPhotoViewModel @Inject constructor(userRepository: UserRepository) :
    BasePhotoViewModel(userRepository) {

}
