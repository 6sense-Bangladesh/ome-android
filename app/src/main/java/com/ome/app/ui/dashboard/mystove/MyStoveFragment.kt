package com.ome.app.ui.dashboard.mystove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.R
import com.ome.app.databinding.FragmentMyStoveBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyStoveFragment :
    BaseFragment<ProfileViewModel, FragmentMyStoveBinding>(FragmentMyStoveBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()
    private var navController: NavController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.navHost) as? NavHostFragment
        navController = navHostFragment?.navController
        binding.test.setOnClickListener {
            //navController.navigate(MyStoveFragmentDirections.actionMyStoveFragmentToKnobWakeUpFragment(false))
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }

    override fun handleBackPressEvent() {}

}
