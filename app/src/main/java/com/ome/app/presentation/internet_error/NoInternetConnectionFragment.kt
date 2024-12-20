package com.ome.app.presentation.internet_error

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.databinding.FragmentNoInternetConnectionBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class NoInternetConnectionFragment :
    BaseFragment<NoInternetConnectionViewModel, FragmentNoInternetConnectionBinding>(
        FragmentNoInternetConnectionBinding::inflate
    ) {

    override val viewModel: NoInternetConnectionViewModel by viewModels()

    private val args by navArgs<NoInternetConnectionFragmentArgs>()

//    override fun setupUI() {
//        if(args.showRetryButton){
//            binding.btnRetry.visible()
//            binding.btnRetry.setBounceClickListener {
//                activity?.recreate()
//            }
//        }
//    }

    override fun handleBackPressEvent() {

    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.connectionStatusListener.connectionStatusFlow.collectWithLifecycle { status ->
            when (status) {
                ConnectionStatusListener.ConnectionStatusState.Default,
                ConnectionStatusListener.ConnectionStatusState.HasConnection,
                ConnectionStatusListener.ConnectionStatusState.Dismissed -> {
                    if(mainViewModel.startDestination.value != null)
                        popBackSafe()
                    else if(args.showRetryButton){
                        if(mainViewModel.startDestinationJob?.isActive.isFalse()) {
                            activity?.recreate()
                        }
                        binding.descriptionTxt.text = getString(R.string.retrying)
//                        binding.btnRetry.gone()
                        binding.loadingProgress.visible()
                    }
                }
                ConnectionStatusListener.ConnectionStatusState.NoConnection -> {
                    if(args.showRetryButton){
                        binding.descriptionTxt.text = getString(R.string.no_internet_connection)
                        binding.loadingProgress.gone()
//                        binding.btnRetry.visible()
                    }
                }
            }
        }

    }
}

@Keep
@Parcelize
data class NoInternetConnectionFragmentParams(val showRetryButton: Boolean = false) : Parcelable
