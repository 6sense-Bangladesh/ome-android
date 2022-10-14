package com.example.inirv.InititalSetup.ISGasOrElectric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class ISGasOrElectricFragment : Fragment() {

    companion object {
        fun newInstance() = ISGasOrElectricFragment()
    }

    private lateinit var viewModel: ISGasOrElectricViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_is_gas_or_electric, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ISGasOrElectricViewModel::class.java)
        // TODO: Use the ViewModel
    }

}