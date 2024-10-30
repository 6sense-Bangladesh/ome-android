package com.ome.app.ui.dashboard.my_stove.device

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.ui.base.BaseFragment


class DeviceDetailsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceViewModel by activityViewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()


    override fun setupUI() {

    }

    override fun setupListener() {

    }

    override fun setupObserver() {
        super.setupObserver()

    }

}