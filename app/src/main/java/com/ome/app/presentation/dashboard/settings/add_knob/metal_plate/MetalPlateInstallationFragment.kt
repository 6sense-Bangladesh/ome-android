package com.ome.app.presentation.dashboard.settings.add_knob.metal_plate

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentMetalPlateBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.scanner.QrCodeScannerParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class MetalPlateInstallationFragment :
    BaseFragment<EmptyViewModel, FragmentMetalPlateBinding>(
        FragmentMetalPlateBinding::inflate
    ) {
    override val viewModel: EmptyViewModel by viewModels()

    private val args by navArgs<MetalPlateInstallationFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        binding.shaftIv.loadDrawable(R.drawable.knob_unplug)
    }


    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setBounceClickListener {
            if(params.selectedKnobPosition == -1) {
                navigateSafe(
                    MetalPlateInstallationFragmentDirections.actionMetalPlateInstallationFragmentToSelectBurnerFragment(
                        SelectBurnerFragmentParams()
                    )
                )
            }
            else{
                navigateSafe(
                    MetalPlateInstallationFragmentDirections.actionMetalPlateInstallationFragmentToQrCodeScannerFragment(
                        QrCodeScannerParams(selectedKnobPosition = params.selectedKnobPosition)
                    )
                )
            }

        }
    }
}

@Keep
@Parcelize
data class MetalPlateInstallationParams(
    val selectedKnobPosition: Int = -1
) : Parcelable
