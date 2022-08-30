package com.example.inirv.Home.Settings.AutoShutOff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class AutoShutoffFragment : Fragment() {

    companion object {
        fun newInstance() = AutoShutoffFragment()
    }

    private lateinit var viewModel: AutoShutoffViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auto_shutoff, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AutoShutoffViewModel::class.java)
        // TODO: Use the ViewModel
    }

}