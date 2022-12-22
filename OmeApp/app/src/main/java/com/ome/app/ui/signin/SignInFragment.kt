package com.ome.app.ui.signin

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.base.BaseFragment
import com.ome.app.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class SignInFragment: BaseFragment<SignInViewModel, FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    override val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

    }

}

