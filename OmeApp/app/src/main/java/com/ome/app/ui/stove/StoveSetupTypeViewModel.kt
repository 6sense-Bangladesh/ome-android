package com.ome.app.ui.stove

import com.ome.app.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupTypeViewModel @Inject constructor() : BaseViewModel() {

    var stoveType = ""

}
