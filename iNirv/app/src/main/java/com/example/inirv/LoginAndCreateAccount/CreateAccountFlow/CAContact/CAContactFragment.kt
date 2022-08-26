package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAContact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class CAContactFragment : Fragment() {

    companion object {
        fun newInstance() = CAContactFragment()
    }

    private lateinit var viewModel: CAContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ca_contact, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CAContactViewModel::class.java)
        // TODO: Use the ViewModel
    }

}