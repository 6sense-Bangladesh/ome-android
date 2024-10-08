package com.ome.app.ui.signup.email

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.databinding.FragmentSignUpEmailAndPasswordBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.signup.password.AuthParams
import com.ome.app.utils.subscribe
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

class SignUpEmailAndPhoneFragment :
    BaseFragment<SignUpEmailAndPhoneViewModel, FragmentSignUpEmailAndPasswordBinding>(
        FragmentSignUpEmailAndPasswordBinding::inflate
    ) {
    override val viewModel: SignUpEmailAndPhoneViewModel by viewModels()

    private val args by navArgs<SignUpEmailAndPhoneFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.continueBtn.setOnClickListener {
            viewModel.validateFields(
                binding.email.getText().trim(),
                binding.phoneInput.getText().trim()
            )
        }
        viewModel.firstName = args.params.firstName
        viewModel.lastName = args.params.lastName
    }


    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.emailAndPassValidationLiveData) {
            findNavController().navigate(
                SignUpEmailAndPhoneFragmentDirections.actionSignUpEmailAndPhoneFragmentToSignUpPasswordFragment(
                    AuthParams(
                        firstName = viewModel.firstName,
                        lastName = viewModel.lastName,
                        email = viewModel.email,
                        phone = viewModel.phoneNumber
                    )
                )
            )
        }
    }
}

@Parcelize
data class NameParams(
    val firstName: String = "",
    val lastName: String = ""
) : Parcelable


