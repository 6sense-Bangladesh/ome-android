package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class CAPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = CAPasswordFragment()
    }

    private lateinit var viewModel: CAPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ca_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CAPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}