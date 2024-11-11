package com.ome.app.presentation.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class MaterialButtonToggleGroupWithRadius @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : MaterialButtonToggleGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val button = getChildAt(i) as MaterialButton
            if (button.visibility == GONE) {
                continue
            }
            val builder = button.shapeAppearanceModel.toBuilder()
            val radius = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
            button.shapeAppearanceModel = builder.setTopLeftCornerSize(radius)
                .setBottomLeftCornerSize(radius)
                .setTopRightCornerSize(radius)
                .setTopLeftCornerSize(radius)
                .setBottomRightCornerSize(radius).build()
        }
    }
}