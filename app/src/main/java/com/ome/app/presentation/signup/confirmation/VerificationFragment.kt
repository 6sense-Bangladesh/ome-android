package com.ome.app.presentation.signup.confirmation

import android.view.KeyEvent
import androidx.core.view.OnReceiveContentListener
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.amplifyframework.kotlin.core.Amplify
import com.ome.app.R
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.databinding.FragmentVerificationBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class VerificationFragment :
    BaseFragment<VerificationViewModel, FragmentVerificationBinding>(
        FragmentVerificationBinding::inflate
    ) {

    override val viewModel: VerificationViewModel by viewModels()

    private val args by navArgs<VerificationFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        binding.textLabel.text = getString(R.string.confirmation_label, params.email.applyMaskToEmail())
        startTimer()
    }

    override fun setupListener() {
        binding.apply {
            btnVerify.setBounceClickListener {
                closeKeyboard()
                binding.btnVerify.startAnimation()
                viewModel.validateConfirmationCode(
                    otp1.text.toString() + otp2.text.toString() +
                            otp3.text.toString() + otp4.text.toString() +
                            otp5.text.toString() + otp6.text.toString()
                )
            }
            btnHelp.setBounceClickListener {
                navigateSafe(VerificationFragmentDirections.actionVerificationFragmentToSupportFragment())
            }
            btnResend.setBounceClickListener {
                binding.loadingLayout.root.visible()
                viewModel.resendCode(params.email.trim())
                listOf(otp1,otp2,otp3,otp4,otp5,otp6).forEach {
                    it.setText("")
                }
//                btnVerify.setSelectionToLast()
            }
            val otpEditTexts = listOf(otp1,otp2,otp3,otp4,otp5,otp6)
            otpEditTexts.toMutableList().apply { removeFirstOrNull() }.forEachIndexed { index, editText ->
                editText.setOnFocusChangeListener { _, _ ->
                    lifecycleScope.launch {
                        delay(1.seconds)
                        val targetIndex = (index downTo 0).find { otpEditTexts[it].length() != 0 } ?: 0
                        if (editText.length() == 0 && editText.hasFocus()) {
                            otpEditTexts[targetIndex].setSelectionToLast()
                        }
                    }
                }
            }
            otpEditTexts.forEach {
                ViewCompat.setOnReceiveContentListener(it, arrayOf("text/*"),
                    OnReceiveContentListener { _, payload ->
                        payload.source.log("payload.source")
                        val oneTimeCode = context.getClipBoardData()
                        oneTimeCode.log("oneTimeCode")
                        if (oneTimeCode.length >= 6 && oneTimeCode.substring(0, 5).hasNumberOnly()) {
                            otp1.setText(oneTimeCode[0].toString())
                            otp1.setSelection(1)
                            otp2.setText(oneTimeCode[1].toString())
                            otp2.setSelection(1)
                            otp3.setText(oneTimeCode[2].toString())
                            otp3.setSelection(1)
                            otp4.setText(oneTimeCode[3].toString())
                            otp4.setSelection(1)
                            otp5.setText(oneTimeCode[4].toString())
                            otp5.setSelection(1)
                            otp6.setText(oneTimeCode[5].toString())
                            otp6.setSelection(1)
                        }
                        return@OnReceiveContentListener null
                    }
                )
            }
            otp1.doOnTextChanged { _, _, _, count ->
                if (count == 1)
                    otp2.setSelectionToLast()
            }
            otp2.doOnTextChanged { _, _, before, count ->
                if (count == 1)
                    otp3.setSelectionToLast()
                else if (count < before)
                    otp1.setSelectionToLast()
            }
            otp3.doOnTextChanged { _, _, before, count ->
                if (count == 1)
                    otp4.setSelectionToLast()
                else if (count < before)
                    otp2.setSelectionToLast()
            }
            otp4.doOnTextChanged { _, _, before, count ->
                if (count == 1)
                    otp5.setSelectionToLast()
                else if (count < before)
                    otp3.setSelectionToLast()
            }
            otp5.doOnTextChanged { _, _, before, count ->
                if (count == 1)
                    otp6.setSelectionToLast()
                else if (count < before)
                    otp4.setSelectionToLast()
            }
            otp6.doOnTextChanged { _, _, before, count ->
                if (count < before)
                    otp5.setSelectionToLast()
            }

            otp1.doAfterTextChanged {
                if (it?.length == 2){
                    otp1.setText(it.first().toString())
                    otp2.setText(it.last().toString())
                    otp2.setSelectionToLast()
                }
            }

            otp2.doAfterTextChanged {
                if (it?.length == 2) {
                    otp2.setText(it.first().toString())
                    otp3.setText(it.last().toString())
                    otp3.setSelectionToLast()
                }
            }
            otp3.doAfterTextChanged {
                if (it?.length == 2) {
                    otp3.setText(it.first().toString())
                    otp4.setText(it.last().toString())
                    otp4.setSelectionToLast()
                }
            }
            otp4.doAfterTextChanged {
                if (it?.length == 2) {
                    otp4.setText(it.first().toString())
                    otp5.setText(it.last().toString())
                    otp5.setSelectionToLast()
                }
            }
            otp5.doAfterTextChanged {
                if (it?.length == 2) {
                    otp5.setText(it.first().toString())
                    otp6.setText(it.last().toString())
                    otp6.setSelectionToLast()
                }
            }

            otp2.setOnKeyListener { _, keyCode, _ ->
                keyCode.log("onKey ${KeyEvent.KEYCODE_DEL}")
                if (keyCode == KeyEvent.KEYCODE_DEL && otp2.selectionStart == 0)
                    otp1.setSelectionToLast()
                false
            }
            otp3.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && otp3.selectionStart == 0)
                    otp2.setSelectionToLast()
                false
            }
            otp4.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && otp4.selectionStart == 0)
                    otp3.setSelectionToLast()
                false
            }
            otp5.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && otp5.selectionStart == 0)
                    otp4.setSelectionToLast()
                false
            }
            otp6.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && otp6.selectionStart == 0)
                    otp5.setSelectionToLast()
                false
            }
        }
        onDismissErrorDialog = {
            binding.loadingLayout.root.gone()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.signUpConfirmationResultLiveData) {
            binding.btnVerify.revertAnimation()
            if (it) {
                AmplifyManager.kotAuth = Amplify.Auth
                navigateSafe(VerificationFragmentDirections.actionVerificationFragmentToWelcomeFragment())
            }

        }
        subscribe(viewModel.resendClickedResultLiveData) {
            binding.loadingLayout.root.gone()
            if(it.isSuccessful) {
                showSuccessDialog(message = getString(R.string.confirmation_label_dialog, params.email.applyMaskToEmail()))
                startTimer()
            }
        }

        subscribe(viewModel.loadingLiveData) {
            binding.btnVerify.revertAnimation()
        }


        subscribe(viewModel.codeValidationLiveData) {
            if (params.isForgotPassword) {
                navigateSafe(VerificationFragmentDirections.actionVerificationFragmentToForgotPasswordFragment(
                    params.apply { code = viewModel.code }
                ))
            } else {
                viewModel.confirmSignUp()
            }
        }

        subscribe(viewModel.loadingLiveData) {
            binding.btnVerify.revertAnimation()
        }
    }

    private var lastTime = 0L
    private val time
        get() = (lastTime.minus(System.currentTimeMillis()) / 1000).toInt()

    private var timerJob: Job? = null

    private fun startTimer(){
        timerJob?.cancel()
        timerJob = viewLifecycleScope.launch {
            lastTime = IO { viewModel.pref.getTimer(Constants.VERIFICATION_KEY) }
            binding.apply {
                if(time > 0) {
                    btnResend.invisible()
                    resendText.visible()
                    resendText.requestFocus()
                }
                while(time >= 0){
                    resendText.text = getString(R.string.resend_after_sec, time.toTimer())
                    delay(1.seconds)
                }
                resendText.invisible()
                btnResend.visible()
            }
        }
    }


    private fun Int.toTimer(): String {
        val minutes = this / 60
        val seconds = this % 60
        return "%02d:%02d".format(minutes, seconds)
    }


}
