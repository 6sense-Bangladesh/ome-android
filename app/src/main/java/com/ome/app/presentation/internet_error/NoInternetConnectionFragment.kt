package com.ome.app.presentation.internet_error

import androidx.fragment.app.viewModels
import com.ome.app.R
import com.ome.app.data.ConnectionListener
import com.ome.app.databinding.FragmentNoInternetConnectionBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoInternetConnectionFragment :
    BaseFragment<NoInternetConnectionViewModel, FragmentNoInternetConnectionBinding>(
        FragmentNoInternetConnectionBinding::inflate
    ) {

    override val viewModel: NoInternetConnectionViewModel by viewModels()

//    override fun setupUI() {
//        if(args.showRetryButton){
//            binding.btnRetry.visible()
//            binding.btnRetry.setBounceClickListener {
//                activity?.recreate()
//            }
//        }
//    }

    override fun handleBackPressEvent() {}

    override fun setupObserver() {
        super.setupObserver()
        viewModel.connectionListener.connectionStatusFlow.collectWithLifecycle { status ->
            when (status) {
                ConnectionListener.State.Default,
                ConnectionListener.State.HasConnection,
                ConnectionListener.State.Dismissed -> {
                    if(mainViewModel.startDestinationJob?.isActive.isFalse()) {
                        activity?.recreate()
                    }
                    binding.descriptionTxt.text = getString(R.string.retrying)
                    binding.descriptionTxt2.gone()
                    binding.loadingProgress.visible()
                }
                ConnectionListener.State.NoConnection -> {
                    binding.descriptionTxt.text = getString(R.string.no_internet_connection)
                    binding.descriptionTxt2.visible()
                    binding.loadingProgress.gone()
                }
            }
        }

    }
}
