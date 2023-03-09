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
import com.ome.app.ui.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.ui.dashboard.settings.add_knob.installation.KnobInstallationManual1FragmentParams
import com.ome.app.ui.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.utils.subscribe
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


        viewModel.knobAngleLiveData.postValue(null)
        viewModel.initSubscriptions()
        viewModel.macAddress = args.params.macAddr
        binding.knobView.setFontSize(14f)
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
        binding.changeWifiCl.setOnClickListener {
            findNavController().navigate(
                DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToConnectToWifiFragment(
                    ConnectToWifiParams(
                        isComeFromSettings = false,
                        isChangeWifiMode = true,
                        macAddrs = args.params.macAddr
                    )
                )
            )
        }
        binding.changeKnobPositionCl.setOnClickListener {
            findNavController().navigate(
                DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToSelectBurnerFragment(
                    SelectBurnerFragmentParams(
                        isChangeMode = true,
                        macAddress = viewModel.macAddress
                    )
                )
            )
        }
    }


    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.zonesLiveData) {
            if (it.rotationDir == 2) {
                binding.knobView.setOffPosition(it.offAngle.toFloat())
                if (it.zones[0].zoneName == "Single") {
                    binding.knobView.setLowSinglePosition(it.zones[0].lowAngle.toFloat())
                    binding.knobView.setHighSinglePosition(it.zones[0].highAngle.toFloat())
                } else {
                    binding.knobView.setLowDualPosition(it.zones[0].lowAngle.toFloat())
                    binding.knobView.setHighDualPosition(it.zones[0].highAngle.toFloat())
                }
                if (it.zones[1].zoneName == "Dual") {
                    binding.knobView.setLowDualPosition(it.zones[1].lowAngle.toFloat())
                    binding.knobView.setHighDualPosition(it.zones[1].highAngle.toFloat())
                } else {
                    binding.knobView.setLowSinglePosition(it.zones[1].lowAngle.toFloat())
                    binding.knobView.setHighSinglePosition(it.zones[1].highAngle.toFloat())
                }
            } else {
                binding.knobView.setOffPosition(it.offAngle.toFloat())
                binding.knobView.setLowSinglePosition(it.zones[0].lowAngle.toFloat())
                binding.knobView.setMediumPosition(it.zones[0].mediumAngle.toFloat())
                binding.knobView.setHighSinglePosition(it.zones[0].highAngle.toFloat())
            }

        }

        subscribe(viewModel.knobAngleLiveData) { angle ->
            angle?.let { binding.knobView.setKnobPosition(it) }
        }
    }

}

@Parcelize
data class DeviceSettingsFragmentParams(val name: String, val macAddr: String) : Parcelable
