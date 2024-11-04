package com.ome.app.presentation.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.ome.app.databinding.OmePhoneInputBinding
import com.ome.app.utils.inflate

class OmePhoneInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding = inflate<OmePhoneInputBinding>()

    private val PHONE_NUMBER_LENGTH = 10

    private companion object {
        const val TYPE_DEFAULT = 0
        const val PASSWORD_VISIBILITY = false
    }


    init {
        initListeners()
    }

    private var mIsDeleting = false
    private var mOnTextChangedCount = 0
    private var mBeforeTextChangedCount = 0


    fun formatString(phone: String, isDeleting: Boolean = false): String {
        var finalString = ""
        phone.forEachIndexed { index, c ->
            if (index + 1 == 3 || index + 1 == 6) {
                finalString += "$c-"

            } else {
                finalString += c
            }
        }
        if (finalString.isNotEmpty()) {
            if (isDeleting && finalString.last() == '-') {
                finalString = finalString.substring(0, finalString.length - 1)
            }
        }
        cursorPosition = finalString.length
        return finalString
    }

    fun getText() = binding.editText.text.toString().trim().replace("-","")

    var cursorPosition = 0
    private fun initListeners() {
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, start: Int, count: Int, after: Int) {
                mBeforeTextChangedCount = p0.length

            }

            override fun onTextChanged(p0: CharSequence, start: Int, count: Int, after: Int) {
                mOnTextChangedCount = p0.length
            }

            override fun afterTextChanged(s: Editable) {
                mIsDeleting = mOnTextChangedCount < mBeforeTextChangedCount

                binding.editText.removeTextChangedListener(this)
                val phone = s.toString().replace("-", "")

                if (!mIsDeleting) {
                    binding.editText.setText(formatString(phone))
                    binding.editText.setSelection(cursorPosition)
                } else {
                    binding.editText.setText(formatString(phone, true))
                    binding.editText.setSelection(cursorPosition)
                }
                binding.editText.addTextChangedListener(this)
            }

        })
    }


    @Suppress("UNCHECKED_CAST")
    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.childrenStates = SparseArray()
        for (i in 0 until childCount) {
            getChildAt(i).saveHierarchyState(ss.childrenStates as SparseArray<Parcelable>)
        }
        return ss
    }

    @Suppress("UNCHECKED_CAST")
    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        for (i in 0 until childCount) {
            getChildAt(i).restoreHierarchyState(ss.childrenStates as SparseArray<Parcelable>)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    class SavedState(superState: Parcelable?) : View.BaseSavedState(superState) {
        var childrenStates: SparseArray<Any>? = null

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            childrenStates?.let {
                out.writeSparseArray(it)
            }
        }
    }
}
