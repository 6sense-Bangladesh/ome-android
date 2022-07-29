package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAName.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class CreateAccountNameFragment(
) : Fragment() {

    var viewModel: ViewModel
        private set

    companion object {
//        fun newInstance(_viewModel: ViewModel) = CreateAccountNameFragment(_viewModel = _viewModel)
        fun newInstance() = CreateAccountNameFragment()
    }

    init {
        viewModel = CreateAccountNameViewModel(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_account_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateAccountNameViewModel::class.java)
        // TODO: Use the ViewModel
    }

}