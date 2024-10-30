package com.ome.app.ui.dashboard.my_stove.device

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.my_stove.MyStoveFragment.Companion.setupKnob
import com.ome.app.utils.onBackPressed


class DeviceDetailsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceViewModel by activityViewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()


    override fun setupUI() {
        binding.apply {
            name.text = args.params.name
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
                knob.setupKnob(it, null)
                knob.setFontSize(18F)
            }
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuDeviceSetting -> {
                    findNavController().navigate(
                        DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceSettingsFragment(
                            DeviceFragmentParams(
                                name = args.params.name,
                                macAddr = args.params.macAddr
                            )
                        )
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()

    }

}