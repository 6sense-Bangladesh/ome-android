package com.example.inirv.InititalSetup.ISComplete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class ISCompleteFragment : Fragment() {

    companion object {
        fun newInstance() = ISCompleteFragment()
    }

    private lateinit var viewModel: ISCompleteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_is_complete, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ISCompleteViewModel::class.java)
        // TODO: Use the ViewModel
    }

}