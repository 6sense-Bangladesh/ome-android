package com.ome.app.presentation.dashboard.settings.add_knob.burner

import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentSelectBurnerBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.scanner.QrCodeScannerParams
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.log
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class SelectBurnerFragment : BaseFragment<SelectBurnerViewModel, FragmentSelectBurnerBinding>(
    FragmentSelectBurnerBinding::inflate
) {
    override val viewModel: SelectBurnerViewModel by viewModels()

    private val args by navArgs<SelectBurnerFragmentArgs>()

    override fun setupUI() {
        viewModel.macAddress = args.params.macAddress
        viewModel.loadData()
        mainViewModel.selectedBurnerIndex?.let {
            viewModel.selectedBurnerIndex = it
        }
//        if(viewModel.selectedBurnerIndex != null && viewModel.stoveOrientation != null){
//            binding.burnerSelectionView.selectBurnerManually(viewModel.selectedBurnerIndex!!, viewModel.stoveOrientation!!)
//        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.burnerSelectionView.onBurnerSelect = { index ->
            viewModel.selectedBurnerIndex = index
        }
        binding.continueBtn.setOnClickListener {
            viewModel.selectedBurnerIndex?.let {
                if (args.params.isEditMode) {
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
                    mainViewModel.selectedBurnerIndex = viewModel.selectedBurnerIndex
                    findNavController().navigate(
                        SelectBurnerFragmentDirections.actionSelectBurnerFragmentToQrCodeScannerFragment(
                            QrCodeScannerParams(selectedKnobPosition = it)
                        )
                    )
                }
            } ?: onError(if(args.params.isEditMode) "Already in this position" else "Please select burner position")
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.selectedIndexes.collectWithLifecycle{
            it.second.log("selectedIndexes")
            binding.burnerSelectionView.initStoveBurners(
                stoveOrientation = it.first,
                selectedBurners = it.second,
                editModeIndex = it.third
            )
            if(viewModel.selectedBurnerIndex != null && viewModel.stoveOrientation != null){
                binding.burnerSelectionView.selectBurnerManually(viewModel.selectedBurnerIndex!!, viewModel.stoveOrientation!!)
            }
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
    val isEditMode: Boolean = false,
    val macAddress: String = "",
) : Parcelable

