package com.ome.app.ui.stove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.app.base.BaseFragment
import com.ome.app.databinding.FragmentStoveSetupBrandBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoveSetupBrandFragment:
    BaseFragment<StoveSetupBrandViewModel, FragmentStoveSetupBrandBinding>(FragmentStoveSetupBrandBinding::inflate) {

    override val viewModel: StoveSetupBrandViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(true)
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}
