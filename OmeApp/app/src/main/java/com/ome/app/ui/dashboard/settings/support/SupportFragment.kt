package com.ome.app.ui.dashboard.settings.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.databinding.FragmentSupportBinding
import com.ome.app.base.BaseFragment
import dev.chrisbanes.insetter.applyInsetter

class SupportFragment :
    BaseFragment<SupportViewModel, FragmentSupportBinding>(FragmentSupportBinding::inflate) {

    override val viewModel: SupportViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.topicSelector.setItems(viewModel.topicsArray)
        viewModel.selectedTopic = viewModel.topicsArray[binding.topicSelector.selectedIndex]
        binding.topicSelector.setOnItemSelectedListener { view, position, id, item ->
            viewModel.selectedTopic = viewModel.topicsArray[position]
        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }

}
