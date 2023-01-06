package com.ome.app.ui.dashboard.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.Ome.databinding.FragmentSettingsBinding
import com.ome.app.base.BaseFragment
import com.ome.app.ui.dashboard.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<ProfileViewModel, FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }

}
