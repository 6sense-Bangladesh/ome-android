package com.ome.app.ui.dashboard.settings.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.R
import com.ome.app.utils.dp

class SettingsItemDecoration :
    RecyclerView.ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position: Int = parent.getChildAdapterPosition(view)
        when (parent.adapter?.getItemViewType(position)) {
            R.layout.settings_title_item -> {
                outRect.top = 24.dp
                outRect.bottom = 24.dp
            }
        }
    }

}

