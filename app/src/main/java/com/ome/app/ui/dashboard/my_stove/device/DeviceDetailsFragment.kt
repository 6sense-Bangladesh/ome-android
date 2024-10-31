package com.ome.app.ui.dashboard.my_stove.device

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.domain.model.network.response.ConnectionState
import com.ome.app.domain.model.network.response.connectionState
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.my_stove.MyStoveFragment.Companion.setupKnob
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.ui.stove.stoveOrientation
import com.ome.app.utils.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


class DeviceDetailsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceViewModel by activityViewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()


    @OptIn(FlowPreview::class)
    override fun setupUI() {
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        binding.apply {
            name.text = context?.getString(R.string.burner_, args.params.stovePosition)
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
                knob.setupKnob(it, null)
                knob.setFontSize(18F)
                val batteryLevel = it.battery
                batteryPercentage.text = batteryLevel.addPercentage()
                it.connectStatus.connectionState.apply {
                    status.text = name
                    when(this){
                        ConnectionState.Charging -> {
                            status.chipBackgroundColor  = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryTransLess)
                            status.chipStrokeWidth = 0F
                        }
                        ConnectionState.Online -> {
                            status.chipBackgroundColor  = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                            status.chipStrokeWidth = 0F
                            status.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                        ConnectionState.Offline -> {
                            status.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
                            status.chipStrokeWidth = 1F
                        }
                    }
                    if(this != ConnectionState.Charging){
                        when (batteryLevel) {
                            in 0 .. 9 -> batteryIcon.setImageResource(R.drawable.ic_battery_0)
                            in 10..20 -> batteryIcon.setImageResource(R.drawable.ic_battery_1)
                            in 21..38 -> batteryIcon.setImageResource(R.drawable.ic_battery_2)
                            in 39..50 -> batteryIcon.setImageResource(R.drawable.ic_battery_3)
                            in 51..62 -> batteryIcon.setImageResource(R.drawable.ic_battery_4)
                            in 63..75 -> batteryIcon.setImageResource(R.drawable.ic_battery_5)
                            in 76..94 -> batteryIcon.setImageResource(R.drawable.ic_battery_6)
                            in 95..100 -> batteryIcon.setImageResource(R.drawable.ic_battery_7)
                        }
                        if(batteryLevel in 0..20)
                            batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                        else
                            batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)
                    }else{
                        batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                        when (batteryLevel) {
                            in 0 .. 20 -> batteryIcon.setImageResource(R.drawable.ic_charging_20)
                            in 21..30 -> batteryIcon.setImageResource(R.drawable.ic_charging_30)
                            in 31..50 -> batteryIcon.setImageResource(R.drawable.ic_charging_50)
                            in 51..60 -> batteryIcon.setImageResource(R.drawable.ic_charging_60)
                            in 61..80 -> batteryIcon.setImageResource(R.drawable.ic_charging_80)
                            in 81..95 -> batteryIcon.setImageResource(R.drawable.ic_charging_90)
                            in 96..100 -> batteryIcon.setImageResource(R.drawable.ic_battery_full)
                        }
                    }
                }
            }
            knob.doOnRotationChange().debounce(500).collectWithLifecycle{ rotation ->
                Log.d(TAG, "doOnRotationChange: $rotation")
            }
            burnerSelection.visible()
            mainViewModel.userInfo.value.stoveOrientation.stoveOrientation.let {
                when(it){
                    StoveOrientation.FOUR_BURNERS -> {
                        visible(knob1, knob2, knob4, knob5, spacer)
                        gone(knob3, knob6, knob7)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob4.backgroundTintList = selectedColor
                            4 -> knob5.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.FOUR_BAR_BURNERS, StoveOrientation.FIVE_BURNERS ->{
                        visible(knob1, knob2, knob4, knob5, knob7)
                        gone(knob3, knob6, spacer)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob4.backgroundTintList = selectedColor
                            4 -> knob5.backgroundTintList = selectedColor
                            5 -> knob7.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.SIX_BURNERS -> {
                        visible(knob1, knob2, knob3, knob4, knob5, knob6, spacer)
                        gone(knob7)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob3.backgroundTintList = selectedColor
                            4 -> knob4.backgroundTintList = selectedColor
                            5 -> knob5.backgroundTintList = selectedColor
                            6 -> knob6.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.TWO_BURNERS_HORIZONTAL ->{
                        visible(knob1, knob2)
                        gone(knob3, knob4, knob5, knob6, knob7, spacer)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.TWO_BURNERS_VERTICAL -> {
                        visible(knob1, knob4, spacer)
                        gone(knob2, knob3, knob5, knob6, knob7)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob4.backgroundTintList = selectedColor
                        }
                    }
                    null -> burnerSelection.gone()
                }
            }
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuDeviceSetting -> {
                    findNavController().navigate(
                        DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceSettingsFragment(
                            DeviceFragmentParams(
                                stovePosition = args.params.stovePosition,
                                macAddr = args.params.macAddr
                            )
                        )
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()

    }

}