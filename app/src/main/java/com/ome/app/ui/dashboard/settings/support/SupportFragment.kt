package com.ome.app.ui.dashboard.settings.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentSupportBinding
import com.ome.app.ui.base.BaseFragment
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
        binding.topicSelector.setOnItemSelectedListener { view, position, id, item ->
            viewModel.selectedTopic = viewModel.topicsArray[position]
        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendBtn.setOnClickListener {
            if (binding.textEt.text.toString().isNotEmpty() && viewModel.selectedTopic.isNotEmpty()) {
                sendMessage()
            }
        }
    }

    private fun sendMessage() =
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(viewModel.supportEmail) +
                        "?subject=" + Uri.encode(viewModel.selectedTopic) +
                        "&body=" + Uri.encode(binding.textEt.text.toString())
            )
        }, "Send mail..."))


    override fun observeLiveData() {
        super.observeLiveData()
    }

}
