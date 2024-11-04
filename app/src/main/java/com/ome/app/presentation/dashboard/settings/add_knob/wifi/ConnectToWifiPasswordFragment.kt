package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.databinding.FragmentConnectToWifiPasswordBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.installation.KnobInstallationManual1FragmentParams
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ConnectToWifiPasswordFragment :
    BaseFragment<ConnectToWifiPasswordViewModel, FragmentConnectToWifiPasswordBinding>(
        FragmentConnectToWifiPasswordBinding::inflate
    ) {
    override val viewModel: ConnectToWifiPasswordViewModel by viewModels()

    private val args by navArgs<ConnectToWifiPasswordFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.connectBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }
        binding.connectBtn.setOnClickListener {
            binding.connectBtn.startAnimation()

            viewModel.password = binding.enterPassword.getText()
            viewModel.sendMessage(
                KnobSocketMessage.TEST_WIFI,
                ssid = viewModel.ssid,
                password = binding.enterPassword.getText(),
                securityType = viewModel.securityType
            )

        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }

        onDismissSuccessDialog = {
            if (args.params.isEditMode) {
                findNavController().popBackStack(R.id.deviceSettingsFragment, false)
            } else {
                findNavController().navigate(
                    ConnectToWifiPasswordFragmentDirections.actionConnectToWifiPasswordFragmentToKnobInstallationManual1Fragment(
                        KnobInstallationManual1FragmentParams(macAddr = args.params.macAddr)
                    )
                )
            }
        }

        val ssid = "\"${args.params.ssid}\""
        binding.ssidTv.text = ssid

        viewModel.macAddr = args.params.macAddr
        viewModel.ssid = args.params.ssid
        viewModel.securityType = args.params.securityType

        viewModel.initListeners()
    }


    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.loadingLiveData) {
            if (it) {
                binding.connectBtn.startAnimation()
            } else {
                binding.connectBtn.revertAnimation()
            }
        }
    }
}

@Parcelize
data class ConnectToWifiPasswordParams(
    val isEditMode: Boolean = true,
    val ssid: String = "",
    val securityType: String = "",
    val macAddr: String = ""
) : Parcelable

