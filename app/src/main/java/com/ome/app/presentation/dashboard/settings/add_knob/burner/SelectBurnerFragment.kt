package com.ome.app.presentation.dashboard.settings.add_knob.burner

import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentSelectBurnerBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.scanner.QrCodeScannerParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class SelectBurnerFragment : BaseFragment<SelectBurnerViewModel, FragmentSelectBurnerBinding>(
    FragmentSelectBurnerBinding::inflate
) {
    override val viewModel: SelectBurnerViewModel by viewModels()

    private val args by navArgs<SelectBurnerFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        viewModel.macAddress = params.macAddress
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
        binding.continueBtn.setBounceClickListener {
            viewModel.selectedBurnerIndex?.let {
                if (params.isEditMode) {
                    showDialog(
                        title = getString(R.string.confirm_position),
                        positiveButtonText = getString(R.string.yes_continue),
                        negativeButtonText = getString(R.string.no_btn),
                        message = SpannableStringBuilder(getString(R.string.confirm_position_body, it.toString())),
                        onPositiveButtonClick = {
                            viewModel.changeKnobPosition(stovePosition = it)
                        }
                    )
                } else {
                    mainViewModel.selectedBurnerIndex = viewModel.selectedBurnerIndex
                    navigateSafe(
                        SelectBurnerFragmentDirections.actionSelectBurnerFragmentToQrCodeScannerFragment(
                            QrCodeScannerParams(selectedKnobPosition = it)
                        )
                    )
                }
            } ?: onError(if(params.isEditMode) getString(R.string.knob_is_already_in_this_position) else getString(R.string.please_select_burner_position))
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
        viewModel.loadingFlow.collectWithLifecycle{
            if (it)
                binding.continueBtn.startAnimation()
            else{
                binding.continueBtn.revertAnimation()
                toast(getString(R.string.knob_position_changed))
                popBackSafe()
            }
        }

    }
}
@Keep
@Parcelize
data class SelectBurnerFragmentParams(
    val isEditMode: Boolean = false,
    val macAddress: String = "",
) : Parcelable

