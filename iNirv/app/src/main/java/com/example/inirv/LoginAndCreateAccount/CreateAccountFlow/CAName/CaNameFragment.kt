package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAName

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.inirv.R

interface CaNameFragmentDelegate{

    fun continueBtnPressed(firstName: String, lastName: String)
}

data class CAUser(
    var firstName: String = "",
    var lastName: String = "",
    var phoneNum: String = "",
    var email: String = "",
    var confirmationCode: String = "",
    var password: String = "",
    var censoredEmail0: String = ""
)

class CaNameFragment : Fragment() {

    companion object {
        fun newInstance() = CaNameFragment()
    }

    private  var viewModel: CaNameViewModel
    private var delegate: CaNameFragmentDelegate

    init {
        viewModel = CaNameViewModel()
        delegate = viewModel
    }

    override fun onStart() {
        super.onStart()

        setup()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ca_name, container, false)
    }

    fun setup(){


        val firstNameET = this.view?.findViewById<EditText>(R.id.fragment_ca_name_first_name_edit_text)

        val lastNameET = this.view?.findViewById<EditText>(R.id.fragment_ca_name_last_name_edit_text)

        this.view?.findViewById<Button>(R.id.fragment_ca_name_continue_button)?.let { continueBtn ->

            continueBtn.setOnClickListener {

                val firstName: String = firstNameET?.text.toString()
                val lastName: String = lastNameET?.text.toString()

                delegate.continueBtnPressed(firstName, lastName)
            }
        }

        if (delegate is CaNameViewModel){

            viewModel.setup()

            // Observe when the names are altered (Currently just looks at the last name
            val nameObserver = Observer<String>{

                val action = CaNameFragmentDirections.actionCreateAccountNameFragmentToCaContactFragment(
                    firstName = viewModel.firstName.value!!,
                    lastName = viewModel.lastName.value!!
                )

                requireParentFragment().findNavController().navigate(action)
            }

            viewModel.lastName.observe(this, nameObserver)

            viewModel.errorMessage.observe(this, Observer<String> { errorMessage ->

                val alertDialogBuilder = AlertDialog.Builder(this.context)

                alertDialogBuilder.setTitle("Warning")
                alertDialogBuilder.setMessage(errorMessage)
                alertDialogBuilder.setNegativeButton("Dismiss", null)

                alertDialogBuilder.show()
            })
        }
    }


}