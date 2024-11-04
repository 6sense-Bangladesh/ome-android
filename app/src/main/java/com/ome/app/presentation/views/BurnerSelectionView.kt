package com.ome.app.presentation.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.ome.app.R
import com.ome.app.databinding.BurnerSelectionViewBinding
import com.ome.app.presentation.stove.StoveOrientation
import com.ome.app.utils.*


class BurnerSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding = inflate<BurnerSelectionViewBinding>()
    private val selectedBurners: ArrayList<Int> = arrayListOf()

    private var lastSelected : MaterialButton? = null
    private val colorSecondary = ContextCompat.getColor(context, R.color.colorSecondary)
    private val colorTertiary = ContextCompat.getColor(context, R.color.colorTertiary)
    private val cardBackground = ContextCompat.getColor(context, R.color.cardBackground)

    var onBurnerSelect: (index: Int) -> Unit = {}
    private var editModeIndex = -1


    fun initStoveBurners(stoveOrientation: StoveOrientation, selectedBurners: List<Int>, editModeIndex: Int) {
        this.selectedBurners.clear()
        this.editModeIndex = editModeIndex
        this.selectedBurners.addAll(selectedBurners)
        editModeIndex.log("editModeIndex")
        if(editModeIndex != -1){
            val allButtons = listOf(binding.button1, binding.button2, binding.button3, binding.button4, binding.button5, binding.button6)
            lastSelected = allButtons[editModeIndex]
        }
        binding.apply {
            when (stoveOrientation) {
                StoveOrientation.FOUR_BURNERS -> {
                    visible(knob1 , knob2 , knob3 , knob4)
                    changeFlexBasisPercent(.5F, knob1 , knob2 , knob3 , knob4)
                    gone(knob5 , knob6)
                    initListeners(button1 to status1 , button2 to status2 , button3 to status3 , button4 to status4)
                }
                StoveOrientation.FIVE_BURNERS, StoveOrientation.FOUR_BAR_BURNERS -> {
                    visible(knob1 , knob2 , knob3 , knob4 , knob5)
                    changeFlexBasisPercent(.5F, knob1 , knob2 , knob4 , knob5)
                    changeFlexBasisPercent(1F,  knob3)
                    gone(knob6)
                    initListeners(button1 to status1 , button2 to status2 , button4 to status4 , button5 to status5 , button3 to status3)
                }
                StoveOrientation.SIX_BURNERS -> {
                    visible(knob1 , knob2 , knob3 , knob4 , knob5 , knob6)
                    changeFlexBasisPercent(.33F, knob1 , knob2 , knob3 , knob4 , knob5 , knob6)
                    initListeners(button1 to status1 , button2 to status2 , button3 to status3 , button4 to status4 , button5 to status5, button6 to status6)
                }
                StoveOrientation.TWO_BURNERS_VERTICAL -> {
                    visible(knob1 , knob2)
                    changeFlexBasisPercent(1F, knob1 , knob2)
                    gone(knob3 , knob4 , knob5 , knob6)
                    initListeners(button1 to status1 , button2 to status2)
                }
                StoveOrientation.TWO_BURNERS_HORIZONTAL -> {
                    visible(knob1 , knob2)
                    changeFlexBasisPercent(.5F, knob1 , knob2)
                    gone(knob3 , knob4 , knob5 , knob6)
                    initListeners(button1 to status1 , button2 to status2)
                }
            }
            flexbox.requestLayout()
        }

    }

    private fun MaterialButton.changeButtonState(isSelected: Boolean, textView: TextView){
        if(isSelected){
            setIconResource(R.drawable.ic_done)
            iconTint = ColorStateList.valueOf(colorTertiary)
            setBackgroundColor(colorSecondary)
            strokeWidth = 0
            textView.text = context.getString(R.string.selected)
        }
        else{
            setIconResource(R.drawable.ic_add)
            iconTint = ColorStateList.valueOf(colorSecondary)
            setBackgroundColor(cardBackground)
            strokeWidth = 1.dp
            textView.text = context.getString(R.string.choose)
        }
    }

    private fun initListeners(vararg pairs: Pair<MaterialButton, TextView>) {
        pairs.forEachIndexed { index, pair ->
            if (selectedBurners.contains(index + 1)) {
                pair.first.changeButtonState(true, pair.second)
            }else{
                pair.first.changeButtonState(false, pair.second)
                pair.first.setBounceClickListener{
                    selectButton(pair, index + 1)
                }
            }
        }
        if(editModeIndex != -1){
            pairs[editModeIndex].first.setBounceClickListener{
                selectButton(pairs[editModeIndex], editModeIndex + 1)
            }
        }
    }

    private fun selectButton(
        pair: Pair<MaterialButton, TextView>,
        position: Int
    ) {
        lastSelected?.changeButtonState(false, pair.second)
        lastSelected = pair.first
        lastSelected?.changeButtonState(true, pair.second)
        onBurnerSelect(position)
    }

    fun selectBurnerManually(position: Int, stoveOrientation: StoveOrientation) {
        binding.apply {
            when(stoveOrientation){
                StoveOrientation.FOUR_BURNERS, StoveOrientation.TWO_BURNERS_HORIZONTAL, StoveOrientation.TWO_BURNERS_VERTICAL -> {
                    when(position){
                        1 -> selectButton(button1 to status1, position)
                        2 -> selectButton(button2 to status2, position)
                        3 -> selectButton(button4 to status4, position)
                        4 -> selectButton(button5 to status5, position)
                    }
                }
                StoveOrientation.FOUR_BAR_BURNERS,StoveOrientation.FIVE_BURNERS, StoveOrientation.SIX_BURNERS -> {
                    when(position){
                        1 -> selectButton(button1 to status1, position)
                        2 -> selectButton(button2 to status2, position)
                        3 -> selectButton(button3 to status3, position)
                        4 -> selectButton(button4 to status4, position)
                        5 -> selectButton(button5 to status5, position)
                        6 -> selectButton(button6 to status6, position)
                    }
                }
            }
        }
    }


//    private fun selectBurner(view: View, index: Int) {
//        for (i in 0 until (view as ConstraintLayout).childCount) {
//            val child = view.getChildAt(i) as ImageView
//            if (child.isSelected) {
//                child.isSelected = false
//                child.setImageResource(R.drawable.ic_burner_not_pressed)
//            }
//        }
//        (view.getChildAt(index) as ImageView).apply {
//            setImageResource(R.drawable.ic_burner_selected_with_plus)
//            isSelected = true
//        }
//    }
//
//    private fun replaceView(res: Int): View {
//        val view = LayoutInflater.from(context).inflate(res, binding.burnersContainerCl, false)
//        binding.burnersContainerCl.removeAllViews()
//        binding.burnersContainerCl.addView(view)
//        return view
//    }

}
