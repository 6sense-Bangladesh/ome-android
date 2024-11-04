package com.ome.app.presentation.dashboard.settings.stove_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ome.app.R


class PhotoSelectionTypeDialogFragment : BottomSheetDialogFragment() {

    var onTakeAPhotoClick: () -> Unit = {}
    var onChooseFromPhoneClick: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.bottom_sheet_photo_selection_fragment, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.closeIv).setOnClickListener { dismiss() }
        view.findViewById<TextView>(R.id.takePhoto).setOnClickListener {
            onTakeAPhotoClick()
            dismiss()
        }
        view.findViewById<TextView>(R.id.chooseFromPhone).setOnClickListener {
            onChooseFromPhoneClick()
            dismiss()
        }
    }
}
