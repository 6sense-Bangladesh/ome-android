package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentConnectToWifiPasswordBinding
import com.ome.app.domain.model.base.DefaultValidation
import com.ome.app.domain.model.base.errorPassword
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.installation.KnobInstallationManualFragmentParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ConnectToWifiPasswordFragment :
    BaseFragment<ConnectToWifiPasswordViewModel, FragmentConnectToWifiPasswordBinding>(
        FragmentConnectToWifiPasswordBinding::inflate
    ) {
    override val viewModel: ConnectToWifiPasswordViewModel by viewModels()

    private val args by navArgs<ConnectToWifiPasswordFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        binding.ssidTv.text = params.ssid

        viewModel.macAddr = params.macAddr
        viewModel.ssid = params.ssid
        viewModel.securityType = params.securityType
    }

    override fun setupListener() {
        viewModel.initListeners()
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.password.doAfterTextChanged {
            binding.passwordLayout.error = null
        }
        binding.connectBtn.setBounceClickListener {
            closeKeyboard()
            binding.connectBtn.startAnimation()
            binding.password.text.toString().let {
                if(it.isBlank()) {
                    binding.connectBtn.revertAnimation()
                    binding.passwordLayout.errorPassword = DefaultValidation.REQUIRED
                }
                else
                    viewModel.testWifi(it)
            }
        }

        onDismissSuccessDialog = {
            mainViewModel.getAllKnobs(3000L)
            if (params.isEditMode) {
                popBackSafe(R.id.deviceSettingsFragment, false)
            } else {
                navigateSafe(
                    ConnectToWifiPasswordFragmentDirections.actionConnectToWifiPasswordFragmentToKnobInstallationManualFragment(
                        KnobInstallationManualFragmentParams(macAddr = params.macAddr)
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

@Keep
@Parcelize
data class ConnectToWifiPasswordParams(
    val isEditMode: Boolean = true,
    val ssid: String = "",
    val securityType: String = "",
    val macAddr: String = ""
) : Parcelable

