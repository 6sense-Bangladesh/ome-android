package com.ome.app.presentation.signup.name

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentSignUpNameBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.signup.email.NameParams
import com.ome.app.utils.subscribe
import dev.chrisbanes.insetter.applyInsetter

class SignUpNameFragment :
    BaseFragment<SignUpNameViewModel, FragmentSignUpNameBinding>(FragmentSignUpNameBinding::inflate) {

    override val viewModel: SignUpNameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }
        binding.continueBtn.setOnClickListener {
            viewModel.validateFirstAndLastName(
                binding.firstName.getText(),
                binding.lastName.getText()
            )
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.firstAndLastNameValidationLiveData){
            findNavController().navigate(
                SignUpNameFragmentDirections.actionSignUpNameFragmentToSignUpEmailAndPasswordFragment(
                    NameParams(binding.firstName.getText(), binding.lastName.getText())
                )
            )
        }
    }
}
