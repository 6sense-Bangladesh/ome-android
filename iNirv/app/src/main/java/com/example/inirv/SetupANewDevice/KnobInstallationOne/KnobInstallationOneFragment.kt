package com.example.inirv.SetupANewDevice.KnobInstallationOne

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class KnobInstallationOneFragment : Fragment() {

    companion object {
        fun newInstance() = KnobInstallationOneFragment()
    }

    private lateinit var viewModel: KnobInstallationOneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_knob_installation_one, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KnobInstallationOneViewModel::class.java)
        // TODO: Use the ViewModel
    }

}