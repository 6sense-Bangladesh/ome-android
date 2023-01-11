package com.ome.app.ui.dashboard.profile

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amulyakhare.textdrawable.TextDrawable
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentProfileBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<ProfileViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {


    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.signOut.setOnClickListener { viewModel.signOut() }

        binding.changePasswordTv.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment) }

        binding.avatarIv.setImageDrawable(
            TextDrawable.builder().beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(
                    "AS",
                    ContextCompat.getColor(requireContext(), R.color.gradient_mid_unpressed_color)
                )
        )
        binding.messageIv.setOnClickListener {
            showMessages()
        }

//        setStatusBarColor(true)
    }

    private fun showMessages() {
        val dialog =
            MessagesBottomSheet()
        dialog.onMessageClick = {
            val str = SpannableStringBuilder("\"${it}\" INVITE you  to join in \"Family #1 Stove\"")
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                1,
                it.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            showDialog(
                message = str,
                positiveButtonText = "Accept",
                negativeButtonText = "Reject",
                onPositiveButtonClick = {},
                onNegativeButtonClick = {}
            )
        }
        dialog.show(
            parentFragmentManager,
            "messages_bottom_sheet"
        )
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }

}
