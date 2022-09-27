package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAContact

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inirv.R
import com.example.inirv.managers.UserManager.email

interface CAContactFragmentDelegate{

    fun continueBtnPressed(email: String, phoneNumber: String)
    fun getInfo(): Map<String, String>
}

class CAContactFragment : Fragment() {

    private val caContactFragmentArgs: CAContactFragmentArgs by navArgs()

    private var firstName: String = ""
    private var lastName: String = ""

    companion object {
        fun newInstance() = CAContactFragment()
    }

    private var viewModel: CAContactViewModel = CAContactViewModel()
    private var delegate: CAContactFragmentDelegate = viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firstName = caContactFragmentArgs.firstName
        lastName = caContactFragmentArgs.lastName
        return inflater.inflate(R.layout.fragment_ca_contact, container, false)
    }

    override fun onStart() {
        super.onStart()

        setup()
    }

    fun setup(){

        // Grab the Edit Texts
        val emailET = this.view?.findViewById<EditText>(R.id.fragment_ca_contact_email_edit_text)
        val phoneET = this.view?.findViewById<EditText>(R.id.fragment_ca_contact_phone_number_edit_text)

        // Setup the continue button
        this.view?.findViewById<Button>(R.id.fragment_ca_contact_continue_button)?.let { continueBtn ->

            continueBtn.setOnClickListener {

                val email: String = emailET?.text.toString()
                val phoneNumber: String = phoneET?.text.toString()

                delegate.continueBtnPressed(email, phoneNumber)
            }
        }

        // Determine the viewmodel
        if (delegate is CAContactViewModel){

            viewModel.setup()

            viewModel.errorMessage.observe(this) { errorMessage ->

                val alertDialogBuilder = AlertDialog.Builder(this.context)

                alertDialogBuilder.setTitle("Warning")
                alertDialogBuilder.setMessage(errorMessage)
                alertDialogBuilder.setNegativeButton("Dismiss", null)

                alertDialogBuilder.show()
            }

            viewModel.email.observe(this){ email

                val infoMap = viewModel.getInfo()

                val action = CAContactFragmentDirections.actionCaContactFragmentToCaPasswordFragment(
                    firstName, lastName, infoMap["email"]!!, infoMap["phoneNumber"]!!
                )

                requireParentFragment().findNavController().navigate(action)
            }
        }
    }

}