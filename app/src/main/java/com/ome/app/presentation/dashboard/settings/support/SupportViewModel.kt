package com.ome.app.presentation.dashboard.settings.support

import com.ome.app.presentation.base.BaseViewModel
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
