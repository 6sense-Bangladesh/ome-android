package com.ome.app.ui.internet_error

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.databinding.FragmentNoInternetConnectionBinding
import com.ome.app.base.BaseFragment
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoInternetConnectionFragment :
    BaseFragment<NoInternetConnectionViewModel, FragmentNoInternetConnectionBinding>(
        FragmentNoInternetConnectionBinding::inflate
    ) {

    override val viewModel: NoInternetConnectionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun handleBackPressEvent() {

    }

    override fun observeLiveData() {
        super.observeLiveData()
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
