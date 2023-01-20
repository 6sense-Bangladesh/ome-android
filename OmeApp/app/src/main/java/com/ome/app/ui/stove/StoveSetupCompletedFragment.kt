package com.ome.app.ui.stove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentStoveSetupCompletedBinding
import com.ome.app.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class StoveSetupCompletedFragment :
    BaseFragment<StoveSetupCompletedViewModel, FragmentStoveSetupCompletedBinding>(
        FragmentStoveSetupCompletedBinding::inflate
    ) {

    override val viewModel: StoveSetupCompletedViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

        binding.imageView2.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.finishBtn.setOnClickListener {
            findNavController().navigate(R.id.action_stoveSetupCompletedFragment_to_myStoveFragment)
        }
        binding.skipKnobSetupBtn.setOnClickListener {

        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}
