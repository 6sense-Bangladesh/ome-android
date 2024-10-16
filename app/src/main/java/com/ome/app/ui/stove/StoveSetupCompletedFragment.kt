package com.ome.app.ui.stove

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupCompletedBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoveSetupCompletedFragment :
    BaseFragment<StoveSetupCompletedViewModel, FragmentStoveSetupCompletedBinding>(
        FragmentStoveSetupCompletedBinding::inflate
    ) {

    override val viewModel: StoveSetupCompletedViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

//        binding.imageView2.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
        binding.appBarLayout.setNavigationOnClickListener(::onBackPressed)
        binding.finishBtn.setBounceClickListener {
            findNavController().navigate(R.id.action_stoveSetupCompletedFragment_to_knobWakeUpFragment)
        }
        binding.skipKnobSetupBtn.setBounceClickListener {
            findNavController().navigate(
                NavDeepLinkRequest.Builder.fromUri(
                    Uri.parse("ome://navigation/dashboard")
                ).build(),
                NavOptions.Builder().setPopUpTo(R.id.dashboardFragment, true).build(), null
            )
//            findNavController().navigate(R.id.action_stoveSetupCompletedFragment_to_dashboardFragment)
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}
