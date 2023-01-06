package com.ome.app.ui.launch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentLaunchBinding
import com.ome.app.base.BaseFragment
import com.ome.app.base.EmptyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchFragment :
    BaseFragment<EmptyViewModel, FragmentLaunchBinding>(FragmentLaunchBinding::inflate) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpNameFragment)
        }
        binding.signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }
    }


}
