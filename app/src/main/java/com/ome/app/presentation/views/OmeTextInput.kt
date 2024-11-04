package com.ome.app.presentation.views

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.ome.app.R
import com.ome.app.databinding.OmeTextInputBinding
import com.ome.app.utils.inflate
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible

class OmeTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding = inflate<OmeTextInputBinding>()

    private companion object {
        const val TYPE_DEFAULT = 0
        const val PASSWORD_VISIBILITY = false
    }

    private var type: Int = TYPE_DEFAULT
    private var isPasswordVisible = PASSWORD_VISIBILITY
    private var hint: String = ""

    init {
        initListeners()
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.OmeInput)
            if (typedArray.hasValue(R.styleable.OmeInput_inputType)) {
                type =
                    typedArray.getInt(R.styleable.OmeInput_inputType, TYPE_DEFAULT)
                when (type) {
                    0 -> {
                        binding.editText.inputType = InputType.TYPE_CLASS_TEXT
                        binding.passwordVisibilityIv.makeGone()
                    }
                    1 -> {
                        binding.editText.inputType = InputType.TYPE_CLASS_NUMBER
                        binding.passwordVisibilityIv.makeGone()
                    }
                    else -> {
                        binding.editText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.editText.typeface = Typeface.DEFAULT
                        binding.passwordVisibilityIv.makeVisible()
                    }

                }
            }
            if (typedArray.hasValue(R.styleable.OmeInput_hint)) {
                hint =
                    typedArray.getString(R.styleable.OmeInput_hint) ?: ""
                binding.editText.hint = hint
            }
            if (typedArray.hasValue(R.styleable.OmeInput_size)) {
                val size =
                    typedArray.getDimensionPixelSize(R.styleable.OmeInput_size, 0)
                binding.editText.textSize = size.toFloat()
            }
            typedArray.recycle()
        }
    }
    fun getText() = binding.editText.text.toString()

    private fun initListeners() {
        binding.passwordVisibilityIv.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            val cursorPosition = binding.editText.selectionStart
            if (isPasswordVisible) {
                binding.passwordVisibilityIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_eye
                    )
                )
                binding.editText.apply {
                    inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    setSelection(cursorPosition)
                    binding.editText.typeface = Typeface.DEFAULT
                }

            } else {
                binding.passwordVisibilityIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_crossed_eye
                    )
                )
                binding.editText.apply {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    setSelection(cursorPosition)
                    binding.editText.typeface = Typeface.DEFAULT
                }


            }
        }
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
