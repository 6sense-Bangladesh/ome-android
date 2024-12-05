package com.ome.app.presentation.launch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ome.app.BuildConfig
import com.ome.app.data.local.PrefKeys
import com.ome.app.databinding.FragmentLaunchBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.presentation.signup.password.AuthParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LaunchFragment :
    BaseFragment<EmptyViewModel, FragmentLaunchBinding>(FragmentLaunchBinding::inflate) {

    override val viewModel: EmptyViewModel by viewModels()

    private var lastTime = 0L
    private val time
        get() = (lastTime.minus(System.currentTimeMillis()) / 1000).toInt()

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            lastTime = IO { viewModel.pref.getTimer(Constants.VERIFICATION_KEY) }
            val params = IO { viewModel.pref.utils.readObject<AuthParams>(PrefKeys.AUTH_PARAMS) }
            params.log("$time")
            if (time > 0 && params != null)
                navigateSafe(LaunchFragmentDirections.actionLaunchFragmentToVerificationFragmentNoBack(params))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAccountBtn.setBounceClickListener {
            navigateSafe(LaunchFragmentDirections.actionWelcomeFragmentToSignUpFragment())
        }
        binding.signInBtn.setBounceClickListener {
            navigateSafe(LaunchFragmentDirections.actionLaunchFragmentToSignInFragment())
        }

        if (BuildConfig.DEBUG) {
            binding.imageView7.setBounceClickListener {
                viewModel.pref.setTimer(Constants.VERIFICATION_KEY, Constants.TWO_MINUTES_MILLIS)
                navigateSafe(LaunchFragmentDirections.actionLaunchFragmentToVerificationFragment(AuthParams(email = "sdosf@jfd.df")))
            }
        }
    }
}
