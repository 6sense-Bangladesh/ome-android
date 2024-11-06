package com.ome.app.presentation.dashboard.settings.auto_shutoff

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentAutoShutOffSettingsBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class AutoShutOffSettingsFragment :
    BaseFragment<AutoShutOffViewModel, FragmentAutoShutOffSettingsBinding>(
        FragmentAutoShutOffSettingsBinding::inflate
    ) {
    override val viewModel: AutoShutOffViewModel by viewModels()

//    private val args by navArgs<AutoShutOffSettingsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.autoShutOffSelector.setItems(viewModel.timeList)
        binding.autoShutOffSelector.setOnItemSelectedListener { view, position, id, item ->
            viewModel.selectedTime = (item as String).replace("Minutes", "").trim().toInt()
        }

        binding.saveBtn.setOnClickListener {
            showDialog(
                title = getString(R.string.warning),
                positiveButtonText = getString(R.string.confirm),
                negativeButtonText = getString(R.string.cancel),
                message = SpannableStringBuilder(getString(R.string.auto_shut_off_time_change_confirmation)),
                onPositiveButtonClick = {
                    viewModel.updateAutoShutOffTime()
                    binding.saveBtn.startAnimation()
                }
            )

        }
        viewModel.loadData()
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.autoShutOffLiveData) {
            binding.autoShutOffSelector.selectedIndex = viewModel.timeList.indexOf(it)
            binding.saveBtn.revertAnimation()
        }
    }
}