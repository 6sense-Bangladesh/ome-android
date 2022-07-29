package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAConfirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class CAConfirmFragment : Fragment() {

    companion object {
        fun newInstance() = CAConfirmFragment()
    }

    private lateinit var viewModel: CAConfirmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ca_confirm, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CAConfirmViewModel::class.java)
        // TODO: Use the ViewModel
    }

}