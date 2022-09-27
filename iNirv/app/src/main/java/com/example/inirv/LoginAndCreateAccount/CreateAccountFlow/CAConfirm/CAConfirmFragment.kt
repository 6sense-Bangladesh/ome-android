package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAConfirm

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inirv.R

interface CAConfirmFragmentDelegate {

    fun confirmPressed(confirmationCode: String)
    fun resendConfCode()
    fun onStart()
}

class CAConfirmFragment : Fragment() {

    private val caConfirmFragmentArgs: CAConfirmFragmentArgs by navArgs()

    companion object {
        fun newInstance() = CAConfirmFragment()
    }

    private lateinit var viewModel: CAConfirmViewModel
    private lateinit var delegate: CAConfirmFragmentDelegate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = CAConfirmViewModel(
            email = caConfirmFragmentArgs.email,
            censoredEmail = caConfirmFragmentArgs.censoredEmail
        )
        delegate = viewModel
        return inflater.inflate(R.layout.fragment_ca_confirm, container, false)
    }

    override fun onStart() {
        super.onStart()

        setup()
    }

    fun setup(){

        // Setup the outlets
        val censoredEmailTV = this.view?.findViewById<TextView>(R.id.fragment_ca_confirm_censored_email_text_view)
        val confirmationCodeET = this.view?.findViewById<EditText>(R.id.fragment_ca_confirm_edit_text)

        // Setup the views
        this.view?.findViewById<Button>(R.id.fragment_ca_confirm_confirm_button)?.let { confirmBtn ->

            confirmBtn.setOnClickListener {
                delegate.confirmPressed(confirmationCodeET?.text.toString())
            }

        }

        // Determine the viewmodel
        if (delegate is CAConfirmViewModel){

            viewModel.onStart()

            viewModel.errorMessage.observe(this) { errorMessage ->

                val alertDialogBuilder = AlertDialog.Builder(this.context)

                alertDialogBuilder.setTitle("Warning")
                alertDialogBuilder.setMessage(errorMessage)
                alertDialogBuilder.setNegativeButton("Dismiss", null)

                alertDialogBuilder.show()
            }

            viewModel.confirmWasSentMsg.observe(this){ confirmSentMsg ->

                // Show the confirmation code was sent message
                val alertDialogBuilder = AlertDialog.Builder(this.context)

                alertDialogBuilder.setTitle("Code Sent")
                alertDialogBuilder.setMessage(confirmSentMsg)
                alertDialogBuilder.setNegativeButton("Dismiss", null)

                alertDialogBuilder.show()

            }

            viewModel.setupMLD.observe(this){

                // Setup the confirmation message that shows the censored email below the confirm button
                /**
                 * TODO: Need to add this in for change password/forgot password flows
                 */
                censoredEmailTV?.text = "Confirmation code sent via email to: ${caConfirmFragmentArgs.censoredEmail} \nMake sure to check your spam folder"

            }

            viewModel.forgotPasswordFlow.observe(this){

                // Go to the password screen
                val action = CAConfirmFragmentDirections.actionGlobalCaPasswordFragment()

                requireParentFragment().findNavController().navigate(action)
            }

            viewModel.confirmationSuccessful.observe(this){

                // Confirmation was succesfull
                val action = CAConfirmFragmentDirections.actionCaConfirmFragmentToCaSuccessFragment(
                    email = caConfirmFragmentArgs.email,
                    password = caConfirmFragmentArgs.password
                )

                requireParentFragment().findNavController().navigate(action)
            }


        }
    }

}