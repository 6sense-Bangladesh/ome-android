package com.ome.app.presentation.dashboard.my_stove

import android.text.SpannableStringBuilder
import androidx.fragment.app.viewModels
import com.google.android.gms.common.util.DeviceProperties.isTablet
import com.ome.app.R
import com.ome.app.databinding.FragmentMyStoveBinding
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.state.Rotation
import com.ome.app.domain.model.state.StoveOrientation
import com.ome.app.domain.model.state.stoveOrientation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.DashboardFragmentDirections
import com.ome.app.presentation.dashboard.my_stove.device.DeviceFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.wake_up.KnobWakeUpParams
import com.ome.app.presentation.views.KnobView
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel


@AndroidEntryPoint
class MyStoveFragment :
    BaseFragment<MyStoveViewModel, FragmentMyStoveBinding>(FragmentMyStoveBinding::inflate) {

    override val viewModel: MyStoveViewModel by viewModels()

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
                            parentFragment?.navigateSafe(
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
            btnTurnOff.setBounceClickListener {
                showDialog(
                    message = SpannableStringBuilder(getString(R.string.confirm_knob_turn_off)),
                    positiveButtonText = getString(R.string.yes_turn_off),
                    negativeButtonText = getString(R.string.no_btn),
                    onPositiveButtonClick = {
                        mainViewModel.turnOffAllKnobs()
                    })
            }
        }
    }

    private fun KnobView.setupKnob(knob: KnobDto) {
        setBounceClickListener{
            parentFragment?.navigateSafe(
                DashboardFragmentDirections.actionDashboardFragmentToDeviceDetailsFragment(
                    DeviceFragmentParams(knob.macAddr)
                )
            )
        }
        stovePosition = knob.stovePosition
        if(changeKnobStatus(knob)) {
            val calibration = knob.calibration.toCalibration(knob.calibrated)
            calibration.zone1?.let { zone ->
                setHighSinglePosition(zone.highAngle.toFloat())
                if(calibration.rotation != Rotation.DUAL)
                    setMediumPosition(zone.mediumAngle.toFloat())
                setLowSinglePosition(zone.lowAngle.toFloat())
            }
            calibration.zone2?.let { zone ->
                setHighDualPosition(zone.highAngle.toFloat())
                setLowDualPosition(zone.lowAngle.toFloat())
            }
            setOffPosition(calibration.offAngle.toFloat())
            setKnobPosition(knob.angle.toFloat())
        }
    }


    private fun initKnob(vararg knob : KnobView) {
        knob.forEach {
            it.resetKnobState()
            if(isTablet(resources))
                it.setFontSize(9f.sp)
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
                        listOf(knob1 , knob2 , knob4 , knob5 , knob3).forEachIndexed { index, knobView -> knobView.stovePosition = index + 1 }
                        visible(knob1 , knob2 , knob4 , knob5 , knob3)
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
            }
            var getKnobStateScope : CoroutineScope? = null
            val listOfFiveBurners = listOf(knob1, knob2, knob4, knob5, knob3)
            val listOfSixBurners = listOf(knob1, knob2, knob3, knob4, knob5, knob6)
            mainViewModel.stoveRepository.knobsFlow.collectWithLifecycle {knobs->
                getKnobStateScope?.cancel()
                val currentStovePositions = knobs.map { it.stovePosition }
                currentStovePositions.log("stovePositions current")
                listOf(1,2,3,4,5,6).minus(currentStovePositions.toSet()).apply { log("stovePositions minus") }.forEach {
                    when(mainViewModel.userInfo.value.stoveOrientation.stoveOrientation){
                        StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS ->
                            listOfFiveBurners.getOrNull(it-1)?.resetKnobState()
                        else ->
                            listOfSixBurners.getOrNull(it-1)?.resetKnobState()
                    }
                }
                knobs.forEach { knob->
                    when(mainViewModel.userInfo.value.stoveOrientation.stoveOrientation){
                        StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS ->
                            listOfFiveBurners.getOrNull(knob.stovePosition-1)?.setupKnob(knob)
                        else ->
                            listOfSixBurners.getOrNull(knob.stovePosition-1)?.setupKnob(knob)
                    }
                    mainViewModel.getKnobStateByMac(knob.macAddr).collectWithLifecycleNoRepeat {knobState ->
                        getKnobStateScope = this
                        if(knobState.angle == null) return@collectWithLifecycleNoRepeat
                        when(mainViewModel.userInfo.value.stoveOrientation.stoveOrientation){
                            StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
                                listOfFiveBurners.getOrNull(knob.stovePosition - 1)
                                    ?.changeKnobState(knobState, knob.calibration.toCalibration(knob.calibrated))
                            }
                            else -> {
                                listOfSixBurners.getOrNull(knob.stovePosition - 1)
                                    ?.changeKnobState(knobState, knob.calibration.toCalibration(knob.calibrated))
                            }
                        }
                        /*if(knobState.knobSetSafetyMode.isTrue()){
//                            knob.calibration.offAngle.apply {
//                                binding.knob1.
//                                binding.knob2.setKnobPosition(this.toFloat())
//                                binding.knob3.setKnobPosition(this.toFloat())
//                                binding.knob4.setKnobPosition(this.toFloat())
//                                binding.knob5.setKnobPosition(this.toFloat())
//                                binding.knob6.setKnobPosition(this.toFloat())
//                            }
                            *//*viewModel.currentKnob.value?.calibration?.offAngle?.let {
                                binding.knob1.setKnobPosition(it.toFloat())
                                binding.knob2.setKnobPosition(it.toFloat())
                                binding.knob3.setKnobPosition(it.toFloat())
                                binding.knob4.setKnobPosition(it.toFloat())
                                binding.knob5.setKnobPosition(it.toFloat())
                                binding.knob6.setKnobPosition(it.toFloat())
                            }*//*
                        }*/
                    }
                }
            }
        }
    }

    override fun handleBackPressEvent() {}

}
