package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAPassword

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inirv.Enums.Userflow
import com.example.inirv.R

interface CAPasswordFragmentDelegate{

    fun continueBtnPressed(password: String, confirmPassword: String)
    fun setup()
}

class CAPasswordFragment(
    userflowString: String = ""
) : Fragment() {

    companion object {
        fun newInstance() = CAPasswordFragment()
    }

    private val caPasswordFragmentArgs: CAPasswordFragmentArgs by navArgs()
    private lateinit var viewModel: CAPasswordViewModel
    private lateinit var delegate: CAPasswordFragmentDelegate
    private val userflow: Userflow

    init {

        userflow = when(userflowString){
            "forgotPassword" -> Userflow.forgotPassword
            "changePassword" -> Userflow.changePassword
            else -> Userflow.createAccount
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = CAPasswordViewModel(
            firstName = caPasswordFragmentArgs.firstName,
            lastName = caPasswordFragmentArgs.lastName,
            email = caPasswordFragmentArgs.email,
            phoneNumber = caPasswordFragmentArgs.phoneNumber,
            sharedPreferences = activity?.getSharedPreferences("omePreferences", Context.MODE_PRIVATE)
        )
        delegate = viewModel
        return inflater.inflate(R.layout.fragment_ca_password, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Get the resources
        val passwordET = this.view?.findViewById<EditText>(R.id.fragment_ca_password_password_edit_text)
        val confPasswordET = this.view?.findViewById<EditText>(R.id.fragment_ca_password_confirm_password_edit_text)

        // Setup the continue button
        this.view?.findViewById<Button>(R.id.fragment_ca_password_continue_button)?.let { continueBtn ->

            continueBtn.setOnClickListener {

                val password: String = passwordET?.text.toString()
                val confPassword: String = confPasswordET?.text.toString()

                delegate.continueBtnPressed(password, confPassword)
            }
        }

        // Determine what to do based on the viewmodel in use
        if (delegate is CAPasswordViewModel){

            viewModel.setup()

            // Setup observers
            viewModel.errorMessage.observe(this){ errorMessage ->

                val alertDialogBuilder = AlertDialog.Builder(this.context)

                alertDialogBuilder.setTitle("Warning")
                alertDialogBuilder.setMessage(errorMessage)
                alertDialogBuilder.setNegativeButton("Dismiss", null)

                alertDialogBuilder.show()
            }

            viewModel.confMessage.observe(this){ confMessage ->

                val confirmAlertDialogBuilder = AlertDialog.Builder(this.context)

                confirmAlertDialogBuilder.setTitle("Code Sent")
                confirmAlertDialogBuilder.setMessage("Code sent via email to: $confMessage")
                confirmAlertDialogBuilder.setNegativeButton("Dismiss") {dialogInterface, int ->

                    // TODO: Need to find a thread safe way of getting the password
                    val action = CAPasswordFragmentDirections.actionCaPasswordFragmentToCaConfirmFragment(
                        caPasswordFragmentArgs.firstName,
                        caPasswordFragmentArgs.lastName,
                        caPasswordFragmentArgs.email,
                        caPasswordFragmentArgs.phoneNumber,
                        true,
                        confMessage,
                        passwordET?.text.toString()
                    )

                    requireParentFragment().findNavController().navigate(action)
                }

                confirmAlertDialogBuilder.show()
            }
        }
    }

}