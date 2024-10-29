package com.ome.app.ui.dashboard.my_stove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.R
import com.ome.app.databinding.FragmentMyStoveBinding
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.DashboardFragmentDirections
import com.ome.app.ui.dashboard.profile.ProfileViewModel
import com.ome.app.ui.dashboard.settings.device.DeviceSettingsFragmentParams
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.ui.stove.stoveOrientation
import com.ome.app.ui.views.KnobView
import com.ome.app.utils.*
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
//        binding.test.setOnClickListener {
            //navController.navigate(MyStoveFragmentDirections.actionMyStoveFragmentToKnobWakeUpFragment(false))
//        }
    }

    override fun setupUI() {
        binding.apply {
            initKnob(knob1, knob2, knob3, knob4, knob5, knob6, knob1center, knob2center)
        }
    }

    override fun setupListener() {
        binding.apply {
            listOf(knob1, knob2, knob3, knob4, knob5, knob6, knob1center, knob2center).forEach { knobView->
                knobView.setBounceClickListener{
                    if(knobView.knobState == KnobView.KnobState.ADD)
//                        navController?.navigate(DashboardFragmentDirections.actionDashboardFragmentToAddKnobNavGraph())
                        navController?.navigate(R.id.action_dashboardFragment_to_addKnobNavGraph)
                }
            }
        }
    }

    private fun KnobView.setupKnob(knob: KnobDto) {
        setBounceClickListener{
            navController?.navigate(
                DashboardFragmentDirections.actionDashboardFragmentToDeviceSettingsFragment(
                    DeviceSettingsFragmentParams(
                        name = "Knob #${knob.stovePosition}",
                        macAddr = knob.macAddr
                    )
                )
            )
        }
        changeKnobState(KnobView.KnobState.NORMAL)
        val calibration = knob.calibration.toCalibration()
        setStovePosition(knob.stovePosition)
        setKnobPosition(knob.angle.toFloat(), calibration.rotationClockWise)
        setOffPosition(calibration.offAngle.toFloat())
        calibration.zones1?.let { zone ->
            setHighSinglePosition(zone.highAngle.toFloat())
            setMediumPosition(zone.mediumAngle.toFloat())
            setLowSinglePosition(zone.lowAngle.toFloat())
        }
        calibration.zones2?.let { zone ->
            setHighDualPosition(zone.highAngle.toFloat())
//            setMediumDualPosition(zone.mediumAngle.toFloat())
            setLowDualPosition(zone.lowAngle.toFloat())
        }
        changeKnobStatus(knob)
    }

    private fun initKnob(vararg know : KnobView) {
        know.forEach {
            it.changeKnobState(KnobView.KnobState.ADD)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        binding.apply {
            mainViewModel.userInfo.collectWithLifecycle {userInfo->
                userInfo.log("userInfo")
                when(userInfo.stoveOrientation.stoveOrientation){
                    StoveOrientation.FOUR_BURNERS -> {
                        visible(knob1, knob2, knob3, knob4)
                        gone(knob5, knob6, knob1centerView, knob2centerView)
                    }
                    StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
                        visible(knob1, knob2, knob3, knob4, knob1centerView)
                        gone(knob5, knob6, knob2centerView)
                    }
                    StoveOrientation.SIX_BURNERS -> {
                        visible(knob1, knob2, knob3, knob4, knob5, knob6)
                        gone(knob1centerView, knob2centerView)
                    }
                    StoveOrientation.TWO_BURNERS_VERTICAL -> {
                        visible(knob1centerView, knob2centerView)
                        gone(knob1, knob2, knob3, knob4, knob5, knob6)
                    }
                    StoveOrientation.TWO_BURNERS_HORIZONTAL -> {
                        visible(knob1, knob2)
                        gone(knob3, knob4, knob5, knob6, knob1centerView, knob2centerView)
                    }
                    null -> {
                        gone(knob1, knob2, knob3, knob4, knob5, knob6, knob1centerView, knob2centerView)
                    }
                }
            }
            mainViewModel.knobs.collectWithLifecycle {knobs->
                knobs.forEach { knob->
                    when(mainViewModel.userInfo.value.stoveOrientation.stoveOrientation){
                        StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
                            when(knob.stovePosition){
                                1 -> knob1.setupKnob(knob)
                                2 -> knob2.setupKnob(knob)
                                3 -> knob3.setupKnob(knob)
                                4 -> knob4.setupKnob(knob)
                                5 -> knob1center.setupKnob(knob)
                            }
                        }
                        StoveOrientation.TWO_BURNERS_VERTICAL -> {
                            when(knob.stovePosition){
                                1 -> knob1center.setupKnob(knob)
                                2 -> knob2center.setupKnob(knob)
                            }
                        }
                        else -> {
                            when(knob.stovePosition){
                                1 -> knob1.setupKnob(knob)
                                2 -> knob2.setupKnob(knob)
                                3 -> knob3.setupKnob(knob)
                                4 -> knob4.setupKnob(knob)
                                5 -> knob5.setupKnob(knob)
                                6 -> knob6.setupKnob(knob)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleBackPressEvent() {}

}
