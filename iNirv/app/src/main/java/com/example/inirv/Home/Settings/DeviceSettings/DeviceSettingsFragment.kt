package com.example.inirv.Home.Settings.DeviceSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class DeviceSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = DeviceSettingsFragment()
    }

    private lateinit var viewModel: DeviceSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeviceSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}