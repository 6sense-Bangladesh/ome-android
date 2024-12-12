package com.ome.app.presentation.stove

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupTypeBinding
import com.ome.app.domain.model.state.StoveType
import com.ome.app.domain.model.state.stoveType
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupTypeFragment :
    BaseFragment<StoveSetupTypeViewModel, FragmentStoveSetupTypeBinding>(FragmentStoveSetupTypeBinding::inflate) {

    override val viewModel: StoveSetupTypeViewModel by viewModels()

    private val args by navArgs<StoveSetupTypeFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        if (params.isEditMode) {
            binding.continueBtn.text = getString(R.string.save)
            mainViewModel.userInfo.value.stoveType
        }else{
            mainViewModel.stoveData.stoveType
        }.also {
            viewModel.stoveType = it
            it.log("stoveType")
            when(it){
                StoveType.GAS_TOP -> binding.gasTop.isChecked = true
                StoveType.ELECTRIC_TOP -> binding.electricTop.isChecked = true
                StoveType.GAS_RANGE -> binding.gasRange.isChecked = true
                StoveType.ELECTRIC_RANGE -> binding.electricRange.isChecked = true
                null -> Unit
            }
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            mainViewModel.stoveData.stoveGasOrElectric = viewModel.stoveType?.type
            mainViewModel.stoveData.stoveKnobMounting = viewModel.stoveType?.mounting

            if(mainViewModel.stoveData.stoveType == null)
                onError("Please select stove type")
            else if (params.isEditMode) {
                binding.continueBtn.startAnimation()
                viewModel.saveStoveType(mainViewModel.userInfo.value.stoveId, onEnd = mainViewModel::getUserInfo)
            }
            else{
                mainViewModel.stoveData.stoveType.isNotNull {
                    navigateSafe(
                        StoveSetupTypeFragmentDirections.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment(
                            StoveSetupPhotoArgs(params.brand, it)
                        )
                    )
                } ?: onError("Please select stove type")

            }

        }
        binding.stoveShipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when(checkedIds.first()){
                R.id.gasTop -> viewModel.stoveType = StoveType.GAS_TOP
                R.id.electricTop -> viewModel.stoveType = StoveType.ELECTRIC_TOP
                R.id.gasRange -> viewModel.stoveType = StoveType.GAS_RANGE
                R.id.electricRange -> viewModel.stoveType = StoveType.ELECTRIC_RANGE
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle{
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                if (params.isEditMode) {
                    toast(getString(R.string.stove_type_changed))
                    onBackPressed()
                }
            }
        }
    }
}

@Keep
@Parcelize
data class StoveSetupTypeArgs(
    val brand: String = "",
    val isEditMode: Boolean = false
) : Parcelable
