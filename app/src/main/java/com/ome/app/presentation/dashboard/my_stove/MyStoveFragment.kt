package com.ome.app.presentation.dashboard.my_stove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.R
import com.ome.app.databinding.FragmentMyStoveBinding
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.state.StoveOrientation
import com.ome.app.domain.model.state.stoveOrientation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.DashboardFragmentDirections
import com.ome.app.presentation.dashboard.my_stove.device.DeviceDetailsFragmentParams
import com.ome.app.presentation.dashboard.profile.ProfileViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.wake_up.KnobWakeUpParams
import com.ome.app.presentation.views.KnobView
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
            initKnob(knob1, knob2, knob3, knob4, knob5, knob6)
        }
    }

    override fun setupListener() {
        binding.apply {
            listOf(knob1, knob2, knob3, knob4, knob5, knob6).forEach { knobView->
                knobView.setBounceClickListener{
                    if(knobView.isKnobInAddState){
                        if(mainViewModel.userInfo.value.stoveSetupComplete.isTrue()) {
                            navController?.navigateSafe(
                                DashboardFragmentDirections.actionDashboardFragmentToKnobWakeUpFragment(
                                    KnobWakeUpParams(selectedKnobPosition = knobView.stovePosition)
                                )
                            )
                        }else toast("Complete stove setup first.")
                    }
//                        navController?.navigateSafe(R.id.action_dashboardFragment_to_addKnobNavGraph)
//                        navController?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToAddKnobNavGraph())
                }
            }
        }
    }
    private fun KnobView.setupKnob(knob: KnobView.KnobState) {

    }
    private fun KnobView.setupKnob(knob: KnobDto, navController: NavController?) {
        navController?.apply {
            setBounceClickListener{
                navController.navigate(
                    DashboardFragmentDirections.actionDashboardFragmentToDeviceDetailsFragment(
                        DeviceDetailsFragmentParams(knob.macAddr)
                    )
                )
            }
        }
        stovePosition = knob.stovePosition
        if(changeKnobStatus(knob)) {
            val calibration = knob.calibration.toCalibration()
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
            setOffPosition(calibration.offAngle.toFloat())
            setKnobPosition(knob.angle.toFloat(), calibration.rotationClockWise)
        }
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
                when (userInfo.stoveOrientation.stoveOrientation) {
                    StoveOrientation.FOUR_BURNERS -> {
                        listOf(knob1, knob2, knob3, knob4).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
                        visible(knob1 , knob2 , knob3 , knob4)
                        changeFlexBasisPercent(.5F, knob1 , knob2 , knob3 , knob4)
                        gone(knob5 , knob6)
                    }
                    StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
                        listOf(knob1 , knob2 , knob3 , knob4 , knob5).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
                        visible(knob1 , knob2 , knob3 , knob4 , knob5)
                        changeFlexBasisPercent(.5F, knob1 , knob2 , knob4 , knob5)
                        changeFlexBasisPercent(1F,  knob3)
                        gone(knob6)
                    }
                    StoveOrientation.SIX_BURNERS -> {
                        listOf(knob1 , knob2 , knob3 , knob4 , knob5 , knob6).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
                        visible(knob1 , knob2 , knob3 , knob4 , knob5 , knob6)
                        changeFlexBasisPercent(.33F, knob1 , knob2 , knob3 , knob4 , knob5 , knob6)
                    }
                    StoveOrientation.TWO_BURNERS_VERTICAL -> {
                        listOf(knob1 , knob2).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
                        visible(knob1 , knob2)
                        changeFlexBasisPercent(1F, knob1 , knob2)
                        gone(knob3 , knob4 , knob5 , knob6)
                    }
                    StoveOrientation.TWO_BURNERS_HORIZONTAL -> {
                        listOf(knob1 , knob2).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
                        visible(knob1 , knob2)
                        changeFlexBasisPercent(.5F, knob1 , knob2)
                        gone(knob3 , knob4 , knob5 , knob6)
                    }
                    null -> Unit
                }

//                when(userInfo.stoveOrientation.stoveOrientation){
//                    StoveOrientation.FOUR_BURNERS -> {
//                        listOf(knob1, knob2, knob3, knob4).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
//                        visible(knob1, knob2, knob3, knob4)
//                        gone(knob5, knob6, knob1centerView, knob2centerView)
//                    }
//                    StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
//                        listOf(knob1, knob2, knob3, knob4, knob1center).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
//                        visible(knob1, knob2, knob3, knob4, knob1centerView)
//                        gone(knob5, knob6, knob2centerView)
//                    }
//                    StoveOrientation.SIX_BURNERS -> {
//                        visible(knob1, knob2, knob3, knob4, knob5, knob6)
//                        listOf(knob1, knob2, knob3, knob4, knob5, knob6).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
//                        gone(knob1centerView, knob2centerView)
//                    }
//                    StoveOrientation.TWO_BURNERS_VERTICAL -> {
//                        visible(knob1centerView, knob2centerView)
//                        listOf(knob1center, knob2center).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
//                        gone(knob1, knob2, knob3, knob4, knob5, knob6)
//                    }
//                    StoveOrientation.TWO_BURNERS_HORIZONTAL -> {
//                        visible(knob1, knob2)
//                        listOf(knob1, knob2).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
//                        gone(knob3, knob4, knob5, knob6, knob1centerView, knob2centerView)
//                    }
//                    null -> {
//                        gone(knob1, knob2, knob3, knob4, knob5, knob6, knob1centerView, knob2centerView)
//                    }
//                }
            }
            mainViewModel.knobs.collectWithLifecycle {knobs->
                knobs.forEach { knob->
                    mainViewModel.getKnobStateByMac(knob.macAddr).collectWithLifecycleStateIn {
                        it.angle
                    }
                    when(mainViewModel.userInfo.value.stoveOrientation.stoveOrientation){
                        StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
                            when(knob.stovePosition){
                                1 -> knob1.setupKnob(knob, navController)
                                2 -> knob2.setupKnob(knob, navController)
                                3 -> knob4.setupKnob(knob, navController)
                                4 -> knob5.setupKnob(knob, navController)
                                5 -> knob3.setupKnob(knob, navController)
                            }
                        }
                        else -> {
                            when(knob.stovePosition){
                                1 -> knob1.setupKnob(knob, navController)
                                2 -> knob2.setupKnob(knob, navController)
                                3 -> knob3.setupKnob(knob, navController)
                                4 -> knob4.setupKnob(knob, navController)
                                5 -> knob5.setupKnob(knob, navController)
                                6 -> knob6.setupKnob(knob, navController)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleBackPressEvent() {}

}
