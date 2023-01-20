package com.ome.app.ui.dashboard.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ome.Ome.R
import com.ome.app.ui.base.recycler.RecyclerDelegationAdapter
import com.ome.app.ui.dashboard.profile.adapter.MessageAdapter
import com.ome.app.ui.dashboard.profile.adapter.MessageItemModel
import com.ome.app.ui.dashboard.profile.adapter.SimpleDividerItemDecoration


class MessagesBottomSheet : BottomSheetDialogFragment() {
    private val adapter by lazy {
        RecyclerDelegationAdapter(requireContext()).apply {
            addDelegate(MessageAdapter(requireContext()) {
                onMessageClick(it.invitationFrom)
            })
        }
    }
    var onMessageClick: (email: String) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.bottom_sheet_fragment_message, container,
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
                MessageItemModel("alex@gmail.com", false),
                MessageItemModel("jack@gmail.com", false),
                MessageItemModel("clara@gmail.com", true),
                MessageItemModel("test3224@gmail.com", false),
                MessageItemModel("james@gmail.com", true),
                MessageItemModel("ffff@gmail.com", false),
                MessageItemModel("4211@gmail.com", false),
                MessageItemModel("kskkkk@gmail.com", false),
                MessageItemModel("fmfmfmss@gmail.com", false),
                MessageItemModel("1242@gmail.com", false),
                MessageItemModel("fffcxxzz@gmail.com", false),
                MessageItemModel("kfkfksaqaa@gmail.com", false)
            )
        )
    }
}
