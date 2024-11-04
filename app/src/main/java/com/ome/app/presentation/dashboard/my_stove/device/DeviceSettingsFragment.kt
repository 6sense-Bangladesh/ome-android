package com.ome.app.presentation.dashboard.my_stove.device

import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceSettingsBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.presentation.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.presentation.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.direction.DirectionSelectionFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DeviceSettingsFragment :
    BaseFragment<DeviceSettingsViewModel, FragmentDeviceSettingsBinding>(FragmentDeviceSettingsBinding::inflate) {

    override val viewModel: DeviceSettingsViewModel by viewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()

    private val adapter by lazy { SettingItemAdapter(onClick) }

    override fun setupUI() {
        binding.apply {
            viewModel.macAddress = args.params.macAddr
//            name.text = args.params.name
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
                knobView.setKnobPosition(it.angle.toFloat())
//                knobView.setFontSize(18F)
            }
            recyclerView.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.knobAngleLiveData.postValue(null)
        viewModel.initSubscriptions()
//        binding.knobView.setFontSize(14f)
        binding.knobTv.text = getString(R.string.knob_, args.params.stovePosition)
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

//        viewModel.loadingFlow.collectWithLifecycle {
//            binding.loadingLayout.root.changeVisibility(it)
//        }
    }

    private val onClick: (ItemModel) -> Unit = { item ->
        when (item) {
            is DeviceSettingsItemModel -> {
                when (item) {
                    DeviceSettingsItemModel.KnobPosition -> {
                        findNavController().navigate(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToSelectBurnerFragment(
                                SelectBurnerFragmentParams(isEditMode = true, macAddress = viewModel.macAddress)
                            )
                        )
                    }
                    DeviceSettingsItemModel.KnobWiFI -> {
                        findNavController().navigate(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToConnectToWifiFragment(
                                ConnectToWifiParams(isEditMode = true, macAddrs = viewModel.macAddress)
                            )
                        )
                    }
                    DeviceSettingsItemModel.KnobOrientation -> {
                        findNavController().navigate(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToDirectionSelectionFragment(
                                DirectionSelectionFragmentParams(isEditMode = true, macAddress = viewModel.macAddress)
                            )
                        )
                    }
                    DeviceSettingsItemModel.DeleteKnob -> {
                        showDialog(
                            message = SpannableStringBuilder(getString(R.string.confirm_delete)),
                            positiveButtonText = getString(R.string.delete),
                            isRedPositiveButton = true,
                            onPositiveButtonClick = {
                                binding.loadingLayout.root.visible()
                                viewModel.deleteKnob{
                                    findNavController().popBackStack(R.id.dashboardFragment, false)
                                    binding.loadingLayout.root.gone()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Parcelize
data class DeviceSettingsFragmentParams(val stovePosition: Int, val macAddr: String) : Parcelable
