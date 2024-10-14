package com.ome.app.ui.dashboard.mystove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentMyStoveBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyStoveFragment :
    BaseFragment<ProfileViewModel, FragmentMyStoveBinding>(FragmentMyStoveBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.test.setOnClickListener {
            //findNavController().navigate(MyStoveFragmentDirections.actionMyStoveFragmentToKnobWakeUpFragment(false))
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }

}
