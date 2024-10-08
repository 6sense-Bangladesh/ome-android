package com.ome.app.ui.dashboard.settings.support

import com.ome.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor() : BaseViewModel() {

    val topicsArray = listOf(
        "General Question",
        "Issue with Installation",
        "Issue with Connectivity",
        "Issue with App",
        "Other"
    )

    var selectedTopic = ""
    var supportEmail = "contact@inirv.com"
}
