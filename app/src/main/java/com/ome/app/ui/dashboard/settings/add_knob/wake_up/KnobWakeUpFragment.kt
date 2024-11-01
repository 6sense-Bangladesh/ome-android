package com.ome.app.ui.dashboard.settings.add_knob.wake_up

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.ome.app.R
import com.ome.app.databinding.FragmentKnobWakeUpBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.navigate
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.ui.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KnobWakeUpFragment : BaseFragment<KnobWakeUpViewModel, FragmentKnobWakeUpBinding>(FragmentKnobWakeUpBinding::inflate) {

    override val viewModel: KnobWakeUpViewModel by viewModels()

//    private val args by navArgs<KnobWakeUpFragmentArgs>()

    override fun setupUI() {
        binding.label2Tv.movementMethod = LinkMovementMethod.getInstance()
        binding.label2Tv.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setOnClickListener {
            Screens.SelectBurnerPosition.navigate(SelectBurnerFragmentParams())
//            findNavController().navigate(
//                KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToSelectBurnerFragment("")
//            )
        }
    }

    override fun setupObserver() {
        super.setupObserver()

    }
}
