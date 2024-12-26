package com.ome.app.presentation.dashboard.settings.auto_shutoff

import android.text.SpannableStringBuilder
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.ome.app.R
import com.ome.app.databinding.FragmentAutoShutOffSettingsBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoShutOffSettingsFragment :
    BaseFragment<AutoShutOffViewModel, FragmentAutoShutOffSettingsBinding>(
        FragmentAutoShutOffSettingsBinding::inflate
    ) {
    override val viewModel: AutoShutOffViewModel by viewModels()

//    private val args by navArgs<AutoShutOffSettingsFragmentArgs>()

    override fun setupUI() {
        binding.autoShutOffSelector.setSimpleItems(viewModel.timeList.map { it.first }.toTypedArray())
        viewModel.loadData()
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.saveBtn.setBounceClickListener {
            showDialog(
                title = getString(R.string.warning),
                positiveButtonText = "Yes, Change",
                negativeButtonText = getString(R.string.cancel),
                message = SpannableStringBuilder(getString(R.string.auto_shut_off_time_change_confirmation)),
                onPositiveButtonClick = {
                    viewModel.updateAutoShutOffTime()
                },
            )
        }
        binding.autoShutOffSelector.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.selectedTime = viewModel.timeList.getOrNull(position)?.second.orZero()
            }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle{
            if(it)
                binding.saveBtn.startAnimation()
            else {
                binding.saveBtn.revertAnimation()
            }
        }
        subscribe(viewModel.autoShutOffLiveData) { position ->
            val selected = viewModel.timeList.getOrNull(position) ?: viewModel.timeList.first()
            binding.autoShutOffSelector.setText(selected.first)
            viewModel.selectedTime = selected.second
            binding.autoShutOffSelector.setSimpleItems(viewModel.timeList.map { it.first }.toTypedArray())
        }
        subscribe(viewModel.autoShutOffResponseLiveData) {
            binding.saveBtn.revertAnimation()
            toast("Shut off time updated")
            onBackPressed()
        }
    }
}
