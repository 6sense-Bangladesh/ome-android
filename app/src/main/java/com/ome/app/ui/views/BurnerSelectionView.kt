package com.ome.app.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.ome.app.R
import com.ome.app.databinding.BurnerSelectionViewBinding
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.utils.*


class BurnerSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding = inflate<BurnerSelectionViewBinding>()
    private val selectedBurners: ArrayList<Int> = arrayListOf()

    var onBurnerSelect: (index: Int) -> Unit = {}


    fun initStoveBurners(stoveOrientation: StoveOrientation, selectedBurners: List<Int>) {
        this.selectedBurners.clear()
        this.selectedBurners.addAll(selectedBurners)
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

    private var lastSelected : MaterialButton? = null
    private val colorSecondary = ContextCompat.getColor(context, R.color.colorSecondary)
    private val colorTertiary = ContextCompat.getColor(context, R.color.colorTertiary)
    private val cardBackground = ContextCompat.getColor(context, R.color.cardBackground)

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
                    lastSelected?.changeButtonState(false, pair.second)
                    lastSelected = pair.first
                    lastSelected?.changeButtonState(true, pair.second)
                    onBurnerSelect(index + 1 )
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
