package com.ome.app.ui.dashboard.settings.add_knob.burner

import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentSelectBurnerBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.getData
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.ui.dashboard.settings.add_knob.scanner.QrCodeScannerParams
import com.ome.app.utils.isTrue
import com.ome.app.utils.orFalse
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class SelectBurnerFragment : BaseFragment<SelectBurnerViewModel, FragmentSelectBurnerBinding>(
    FragmentSelectBurnerBinding::inflate
) {
    override val viewModel: SelectBurnerViewModel by viewModels()

//    private val args by navArgs<SelectBurnerFragmentArgs>()
    private val args by lazy { Screens.SelectBurnerPosition.getData(arguments) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.continueBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }

        args?.macAddress?.let {
            viewModel.macAddress = it
        }

        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.burnerSelectionView.onBurnerSelect = { index ->
            binding.continueBtn.isEnabled = true
            viewModel.selectedBurnerIndex = index
        }
        binding.continueBtn.setOnClickListener {
            viewModel.selectedBurnerIndex?.let {
                if (args?.isChangeMode.isTrue()) {
                    showDialog(
                        title = getString(R.string.confirm_position),
                        positiveButtonText = getString(R.string.yes_btn),
                        negativeButtonText = getString(R.string.no_btn),
                        message = SpannableStringBuilder(
                            getString(
                                R.string.confirm_position_body,
                                it.toString()
                            )
                        ),
                        onPositiveButtonClick = {
                            binding.continueBtn.startAnimation()
                            viewModel.changeKnobPosition(stovePosition = it)
                        }
                    )
                } else {
                    findNavController().navigate(
                        SelectBurnerFragmentDirections.actionSelectBurnerFragmentToQrCodeScannerFragment(
                            QrCodeScannerParams(
                                isComeFromSettings = args?.isComeFromSettings.orFalse(),
                                selectedIndex = it
                            )
                        )
                    )
                }
            }
        }

        viewModel.loadData()
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.selectedIndexesLiveData) {
            binding.burnerSelectionView.initStoveBurners(it.first, it.second)
        }
        subscribe(viewModel.knobPositionResponseLiveData) {
            binding.continueBtn.revertAnimation()
            showSuccessDialog(message = getString(R.string.change_burner_position_success_body), onDismiss = {
                findNavController().popBackStack()
            })
        }

    }
}

@Parcelize
data class SelectBurnerFragmentParams(
    val isComeFromSettings: Boolean = false,
    val isChangeMode: Boolean = false,
    val macAddress: String = "",
) : Parcelable

