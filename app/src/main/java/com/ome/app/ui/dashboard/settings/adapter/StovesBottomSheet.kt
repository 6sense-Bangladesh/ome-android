package com.ome.app.ui.dashboard.settings.adapter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ome.app.R
import com.ome.app.ui.base.recycler.RecyclerDelegationAdapter
import com.ome.app.ui.dashboard.profile.adapter.SimpleDividerItemDecoration
import com.ome.app.ui.dashboard.settings.adapter.model.StoveItemModel


class StovesBottomSheet : BottomSheetDialogFragment() {
    private val adapter by lazy {
        RecyclerDelegationAdapter(requireContext()).apply {
            addDelegate(StoveAdapter(requireContext()) {
                onStoveClick(it.stove)
            })
        }
    }
    var onStoveClick: (stove: String) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.bottom_sheet_stoves_fragment, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val closeIv = view.findViewById<ImageView>(R.id.closeIv)
        recyclerView.addItemDecoration(
            SimpleDividerItemDecoration(
                requireContext(),
                R.drawable.line_divider
            )
        )
        closeIv.setOnClickListener { dismiss() }
        recyclerView.adapter = adapter
        adapter.setItems(
            listOf(
                StoveItemModel("Family #1 stove"),
                StoveItemModel("Family #2 stove"),
                StoveItemModel("Family #3 stove"),
                StoveItemModel("Family #4 stove"),
                StoveItemModel("Family #5 stove"),
                StoveItemModel("Family #6 stove"),
                StoveItemModel("Family #7 stove"),

            )
        )
    }
}
