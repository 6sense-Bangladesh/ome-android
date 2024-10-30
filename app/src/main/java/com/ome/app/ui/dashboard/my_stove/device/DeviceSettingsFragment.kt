package com.ome.app.ui.dashboard.my_stove.device

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceSettingsBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.my_stove.MyStoveFragment.Companion.setupKnob
import com.ome.app.ui.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.subscribe


class DeviceSettingsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceSettingsBinding>(FragmentDeviceSettingsBinding::inflate) {

    override val viewModel: DeviceViewModel by activityViewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()

    private val adapter by lazy { SettingItemAdapter(onClick) }

    override fun setupUI() {
        binding.apply {
//            name.text = args.params.name
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
                knobView.setupKnob(it, null)
//                knobView.setFontSize(18F)
            }
            recyclerView.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.knobAngleLiveData.postValue(null)
        viewModel.initSubscriptions()
        viewModel.macAddress = args.params.macAddr
        binding.knobView.setFontSize(14f)
        binding.knobTv.text = args.params.name
        binding.macAddressTv.text = getString(R.string.knob_mac_addr_label, args.params.macAddr)

//        binding.changeKnobOrientationCl.setOnClickListener {
//            findNavController().navigate(
//                DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToKnobInstallationManual1Fragment(
//                    KnobInstallationManual1FragmentParams(
//                        isComeFromSettings = true,
//                        macAddr = args.params.macAddr
//                    )
//
//                )
//            )
//        }
//        binding.changeWifiCl.setOnClickListener {
//            findNavController().navigate(
//                DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToConnectToWifiFragment(
//                    ConnectToWifiParams(
//                        isComeFromSettings = false,
//                        isChangeWifiMode = true,
//                        macAddrs = args.params.macAddr
//                    )
//                )
//            )
//        }
//        binding.changeKnobPositionCl.setOnClickListener {
//            Screens.StoveLayout.navigate()
//            findNavController().navigate(
//                DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToSelectBurnerFragment(
//                    SelectBurnerFragmentParams(
//                        isChangeMode = true,
//                        macAddress = viewModel.macAddress
//                    )
//                )
//            )
//        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
    }


    override fun setupObserver() {
        super.setupObserver()
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

        viewModel.deviceSettingsList.collectWithLifecycle {
            adapter.submitList(it)
        }
    }

    private val onClick: (ItemModel) -> Unit = { item ->
        when (item) {
            is SettingsItemModel -> {

            }
        }
    }
}
