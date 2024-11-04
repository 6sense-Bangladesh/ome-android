package com.ome.app.presentation.dashboard.settings.add_knob.wake_up

import android.os.Parcelable
import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentKnobWakeUpBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.scanner.QrCodeScannerParams
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class KnobWakeUpFragment : BaseFragment<KnobWakeUpViewModel, FragmentKnobWakeUpBinding>(FragmentKnobWakeUpBinding::inflate) {

    override val viewModel: KnobWakeUpViewModel by viewModels()

    private val args by navArgs<KnobWakeUpFragmentArgs>()

    override fun setupUI() {
        binding.label2Tv.movementMethod = LinkMovementMethod.getInstance()
        binding.label2Tv.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setOnClickListener {
//            Screens.SelectBurnerPosition.navigate(SelectBurnerFragmentParams())
            if(args.params.selectedKnobPosition == -1) {
                findNavController().navigate(
                    KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToSelectBurnerFragment(SelectBurnerFragmentParams())
                )
            }
            else{
                findNavController().navigate(
                    KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToQrCodeScannerFragment(
                        QrCodeScannerParams(selectedKnobPosition = args.params.selectedKnobPosition)
                    )
                )
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()

    }
}

@Parcelize
data class KnobWakeUpParams(
    val selectedKnobPosition: Int = -1
) : Parcelable
