package com.ome.app.presentation.internet_error

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.databinding.FragmentNoInternetConnectionBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoInternetConnectionFragment :
    BaseFragment<NoInternetConnectionViewModel, FragmentNoInternetConnectionBinding>(
        FragmentNoInternetConnectionBinding::inflate
    ) {

    override val viewModel: NoInternetConnectionViewModel by viewModels()

    override fun handleBackPressEvent() {

    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.connectionStatusListener.connectionStatusFlow) { status ->
            when (status) {
                ConnectionStatusListener.ConnectionStatusState.Default,
                ConnectionStatusListener.ConnectionStatusState.HasConnection,
                ConnectionStatusListener.ConnectionStatusState.Dismissed -> {
                    if(mainViewModel.startDestinationInitialized.value==null){
                        mainViewModel.initStartDestination()
                    } else {
                        findNavController().popBackStack()
                    }
                }
                ConnectionStatusListener.ConnectionStatusState.NoConnection -> {

                }
            }
        }

    }
}
