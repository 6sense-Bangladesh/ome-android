package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.databinding.FragmentConnectToWifiPasswordBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.installation.KnobInstallationManual1FragmentParams
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ConnectToWifiPasswordFragment :
    BaseFragment<ConnectToWifiPasswordViewModel, FragmentConnectToWifiPasswordBinding>(
        FragmentConnectToWifiPasswordBinding::inflate
    ) {
    override val viewModel: ConnectToWifiPasswordViewModel by viewModels()

    private val args by navArgs<ConnectToWifiPasswordFragmentArgs>()

    override fun setupUI() {
        val ssid = "\"${args.params.ssid}\""
        binding.ssidTv.text = ssid

        viewModel.macAddr = args.params.macAddr
        viewModel.ssid = args.params.ssid
        viewModel.securityType = args.params.securityType
    }

    override fun setupListener() {
        viewModel.initListeners()
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.connectBtn.setOnClickListener {
            binding.connectBtn.startAnimation()
            viewModel.password = binding.password.text.toString()
            viewModel.sendMessage(
                message = KnobSocketMessage.TEST_WIFI,
                ssid = viewModel.ssid,
                password = viewModel.password,
                securityType = viewModel.securityType
            )
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

