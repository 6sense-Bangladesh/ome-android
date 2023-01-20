package com.ome.app.ui.dashboard.mystove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.Ome.databinding.FragmentMyStoveBinding
import com.ome.app.base.BaseFragment
import com.ome.app.ui.dashboard.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyStoveFragment :
    BaseFragment<ProfileViewModel, FragmentMyStoveBinding>(FragmentMyStoveBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }

}
