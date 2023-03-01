package com.ome.app.ui.dashboard.settings.device

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentDeviceSettingsBinding
import com.ome.app.base.BaseFragment
import com.ome.app.ui.dashboard.settings.add_knob.installation.KnobInstallationManual1FragmentParams
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize


@AndroidEntryPoint
class DeviceSettingsFragment :
    BaseFragment<DeviceSettingsViewModel, FragmentDeviceSettingsBinding>(
        FragmentDeviceSettingsBinding::inflate
    ) {

    override val viewModel: DeviceSettingsViewModel by viewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.knobTv.text = args.params.name
        binding.macAddressTv.text = getString(R.string.knob_mac_addr_label, args.params.macAddr)
        binding.backIv.setOnClickListener { findNavController().popBackStack() }
        binding.changeKnobOrientationCl.setOnClickListener {
            findNavController().navigate(
                DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToKnobInstallationManual1Fragment(
                    KnobInstallationManual1FragmentParams(
                        isComeFromSettings = true,
                        macAddr = args.params.macAddr
                    )

                )
            )
        }
        binding.changeWifiCl.setOnClickListener { }
        binding.changeKnobPositionCl.setOnClickListener { }
    }


    override fun observeLiveData() {
        super.observeLiveData()
    }

}

@Parcelize
data class DeviceSettingsFragmentParams(val name: String, val macAddr: String) : Parcelable
