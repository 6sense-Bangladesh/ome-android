package com.ome.app.ui.views

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
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
                    else -> {
                        binding.editText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.passwordVisibilityIv.makeVisible()
                    }

                }
            }
            if (typedArray.hasValue(R.styleable.OmeInput_hint)) {
                hint =
                    typedArray.getString(R.styleable.OmeInput_hint) ?: ""
                binding.editText.hint = hint
            }
            typedArray.recycle()
        }
    }


    private fun initListeners() {
        binding.passwordVisibilityIv.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            val cursorPosition = binding.editText.selectionStart
            if (isPasswordVisible) {
                binding.passwordVisibilityIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_crossed_eye
                    )
                )
                binding.editText.apply {
                    inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    setSelection(cursorPosition)
                }

            } else {
                binding.passwordVisibilityIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_eye
                    )
                )
                binding.editText.apply {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    setSelection(cursorPosition)
                }


            }
        }
    }
}
