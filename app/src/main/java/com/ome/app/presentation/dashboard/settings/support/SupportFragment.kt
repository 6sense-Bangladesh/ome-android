package com.ome.app.presentation.dashboard.settings.support

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentSupportBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.onBackPressed

class SupportFragment :
    BaseFragment<SupportViewModel, FragmentSupportBinding>(FragmentSupportBinding::inflate) {

    override val viewModel: SupportViewModel by viewModels()

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
//        binding.autoShutOffSelector.onItemClickListener =
            /*AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.selectedTime = viewModel.timeList.getOrNull(position)?.second.orZero()
            }*/

    }

    override fun setupUI() {

        binding.autoShutOffSelector.setSimpleItems(viewModel.topicsArray.toTypedArray())
    }

/*    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.autoShutOffSelector.setOnItemSelectedListener { view, position, id, item ->
            viewModel.selectedTopic = viewModel.topicsArray[position]
        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendBtn.setOnClickListener {
            if (binding.textEt.text.toString()
                    .isNotEmpty() && viewModel.selectedTopic.isNotEmpty()
            ) {
                sendMessage()
            }
        }
    }*/

    private fun sendMessage() =
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(
                "mailto:" + Uri.encode(viewModel.supportEmail) +
                        "?subject=" + Uri.encode(viewModel.selectedTopic) + ""
//                        "&body=" + Uri.encode(binding.textEt.text.toString())
            )
        }, "Send mail..."))


    override fun setupObserver() {
        super.setupObserver()
    }

}
