package com.ome.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ome.Ome.R
import com.ome.Ome.databinding.BurnerSelectionViewBinding
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.utils.inflate


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
        var view: View? = null
        when (stoveOrientation) {
            StoveOrientation.FOUR_BURNERS -> {
                view = replaceView(R.layout.four_burners_view)
            }
            StoveOrientation.FOUR_BAR_BURNERS -> {
                view = replaceView(R.layout.four_bar_burners_view)
            }
            StoveOrientation.FIVE_BURNERS -> {
                view = replaceView(R.layout.four_bar_burners_view)
            }
            StoveOrientation.SIX_BURNERS -> {
                view = replaceView(R.layout.six_burners_view)
            }
            StoveOrientation.TWO_BURNERS_HORIZONTAL -> {
                view = replaceView(R.layout.two_burners_horizontal_view)
            }
            StoveOrientation.TWO_BURNERS_VERTICAL -> {
                view = replaceView(R.layout.two_burners_horizontal_view)

            }
        }
        initListeners(view)

    }


    private fun initListeners(view: View) {
        for (i in 0 until (view as ConstraintLayout).childCount) {
            val child = view.getChildAt(i) as ImageView
            if (selectedBurners.contains(i + 1)) {
                child.setImageResource(R.drawable.ic_burner_without_plus)
            } else {
                child.setImageResource(R.drawable.ic_burner_not_pressed)
                child.setOnClickListener {
                    selectBurner(view, i)
                    onBurnerSelect(i+1)
                }
            }
        }
    }


    private fun selectBurner(view: View, index: Int) {
        for (i in 0 until (view as ConstraintLayout).childCount) {
            val child = view.getChildAt(i) as ImageView
            if (child.isSelected) {
                child.isSelected = false
                child.setImageResource(R.drawable.ic_burner_not_pressed)
            }
        }
        (view.getChildAt(index) as ImageView).apply {
            setImageResource(R.drawable.ic_burner_selected_with_plus)
            isSelected = true
        }
    }

    private fun replaceView(res: Int): View {
        val view = LayoutInflater.from(context).inflate(res, binding.burnersContainerCl, false)
        binding.burnersContainerCl.removeAllViews()
        binding.burnersContainerCl.addView(view)
        return view
    }

}
