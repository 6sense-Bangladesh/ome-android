package com.ome.app.ui.dashboard.settings

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentSettingsBinding
import com.ome.app.base.BaseFragment
import com.ome.app.ui.base.recycler.RecyclerDelegationAdapter
import com.ome.app.ui.dashboard.settings.adapter.SettingsItemAdapter
import com.ome.app.ui.dashboard.settings.adapter.SettingsItemDecoration
import com.ome.app.ui.dashboard.settings.adapter.SettingsTitleItemAdapter
import com.ome.app.ui.dashboard.settings.adapter.StovesBottomSheet
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<SettingsViewModel, FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override val viewModel: SettingsViewModel by viewModels()

    private val adapter by lazy {
        RecyclerDelegationAdapter(requireContext()).apply {
            addDelegate(SettingsItemAdapter(requireContext()) {

                when(it.option){
                    "Leave Stove"-> {
                        val str =
                            SpannableStringBuilder("Please Confirm that you would like to LEAVE “Family #1 Stove”?")
                        val first = str.indexOf("LEAVE")
                        str.setSpan(
                            ForegroundColorSpan(Color.RED),
                            first,
                            first + 5,
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
                    "Stove Auto-Off Settings"-> {
                        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAutoShutOffSettingsFragment("test"))
                    }
                }
            })
            addDelegate(SettingsTitleItemAdapter(requireContext()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleTv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.rootCl.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(bottom = true)
            }
        }
        binding.supportIv.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_supportFragment)
        }

        binding.stoveSubtitleCl.setOnClickListener { showStoves() }
        viewModel.loadSettings()
        initRecyclerView()
    }


    private fun showStoves() {
        val dialog =
            StovesBottomSheet()
        dialog.onStoveClick = {
        }
        dialog.show(
            parentFragmentManager,
            "stove_bottom_sheet"
        )
    }


    private fun initRecyclerView() {
        binding.recyclerView.addItemDecoration(
            SettingsItemDecoration()
        )
        binding.recyclerView.adapter = adapter
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.settingsList) {
            adapter.setItems(it)
        }
    }

}
