package com.ome.app.presentation.dashboard.settings.support

import android.content.Intent
import android.net.Uri
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentSupportBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.Constants
import com.ome.app.utils.onBackPressed

class SupportFragment :
    BaseFragment<SupportViewModel, FragmentSupportBinding>(FragmentSupportBinding::inflate) {

    override val viewModel: SupportViewModel by viewModels()

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.autoShutOffSelector.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.selectedTopic = viewModel.topicsArray[position]
            }

        binding.sendBtn.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(
                    "mailto:" + Uri.encode(Constants.SUPPORT_EMAIL) +
                            "?subject=" + Uri.encode(viewModel.selectedTopic) +
                            "&body=" + Uri.encode(binding.body.text.toString())
                )
            }, "Send mail..."))
        }

    }

    override fun setupUI() {
        binding.autoShutOffSelector.setText(viewModel.selectedTopic)
        binding.autoShutOffSelector.setSimpleItems(viewModel.topicsArray.toTypedArray())
    }

}
