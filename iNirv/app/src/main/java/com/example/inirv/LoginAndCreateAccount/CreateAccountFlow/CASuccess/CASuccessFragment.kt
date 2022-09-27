package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CASuccess

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inirv.AppLevelNavGraphDirections
import com.example.inirv.R

interface CASuccessFragmentDelegate{

    fun startSetupBtnPressed()
    fun onStart()
}

class CASuccessFragment : Fragment() {

    private val caSuccessFragmentArgs: CASuccessFragmentArgs by navArgs()

    companion object {
        fun newInstance() = Fragment()
    }

    private lateinit var viewModel: CASuccessViewModel
    private lateinit var delegate: CASuccessFragmentDelegate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = CASuccessViewModel(caSuccessFragmentArgs.email, caSuccessFragmentArgs.password)
        delegate = viewModel
        return inflater.inflate(R.layout.fragment_ca_success, container, false)
    }

    override fun onStart() {
        super.onStart()

        setup()
        delegate.onStart()
    }

    private fun setup(){

        // Setup ui elements
        this.view?.findViewById<Button>(R.id.ca_success_fragment_start_setup_btn)?.let { startSetupBtn ->

            startSetupBtn.setOnClickListener {
                delegate.startSetupBtnPressed()
            }
        }

        if (delegate is CASuccessViewModel){

            // Setup observers
            viewModel.errorMessage.observe(this){ errorMessage ->

                val alertDialogBuilder = AlertDialog.Builder(this.context)

                alertDialogBuilder.setTitle("Warning")
                alertDialogBuilder.setMessage(errorMessage)
                alertDialogBuilder.setNegativeButton("Dismiss", null)

                alertDialogBuilder.show()
            }

            viewModel.userManagerSetupComplete.observe(this){ userManagerSetupComplete ->

                if (userManagerSetupComplete){

                    // Go back to the launch screen
                    val action = AppLevelNavGraphDirections.actionGlobalLaunchFragment()

                    requireParentFragment().findNavController().navigate(action)
                }
            }
        }
    }

}