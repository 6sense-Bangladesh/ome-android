package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.databinding.FragmentConnectToWifiPasswordBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.installation.KnobInstallationManualFragmentParams
import com.ome.app.utils.closeKeyboard
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
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
        binding.ssidTv.text = args.params.ssid

        viewModel.macAddr = args.params.macAddr
        viewModel.ssid = args.params.ssid
        viewModel.securityType = args.params.securityType
    }

    override fun setupListener() {
        viewModel.initListeners()
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.connectBtn.setBounceClickListener {
            closeKeyboard()
            binding.connectBtn.startAnimation()
            viewModel.password = binding.password.text.toString()
            viewModel.sendMessageInVM(
                type = KnobSocketMessageType.TEST_WIFI,
                ssid = viewModel.ssid,
                password = viewModel.password,
                securityType = viewModel.securityType
            )
        }

        onDismissSuccessDialog = {
            if (args.params.isEditMode) {
                findNavController().popBackStack(R.id.deviceSettingsFragment, false)
            } else {
                navigateSafe(
                    ConnectToWifiPasswordFragmentDirections.actionConnectToWifiPasswordFragmentToKnobInstallationManualFragment(
                        KnobInstallationManualFragmentParams(macAddr = args.params.macAddr)
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

