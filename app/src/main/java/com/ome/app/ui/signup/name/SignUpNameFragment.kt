package com.ome.app.ui.signup.name

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.databinding.FragmentSignUpNameBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.signup.email.NameParams
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

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.firstAndLastNameValidationLiveData){
            findNavController().navigate(
                SignUpNameFragmentDirections.actionSignUpNameFragmentToSignUpEmailAndPasswordFragment(
                    NameParams(binding.firstName.getText(), binding.lastName.getText())
                )
            )
        }
    }
}
