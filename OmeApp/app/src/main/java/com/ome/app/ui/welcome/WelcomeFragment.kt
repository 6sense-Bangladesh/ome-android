package com.ome.app.ui.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.base.BaseFragment
import com.ome.app.base.EmptyViewModel
import com.ome.app.databinding.FragmentWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment :
    BaseFragment<EmptyViewModel, FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAccountBtn.setOnClickListener { }
        binding.signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }


    }


}
