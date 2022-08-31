package com.example.inirv.Home.Settings.DeviceSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.inirv.R

class DeviceSettingsFragment : Fragment() {

    private val deviceSettingsFragmentArgs: DeviceSettingsFragmentArgs by navArgs()
    private var macID: String = ""

    companion object {
        fun newInstance() = DeviceSettingsFragment()
    }

    private lateinit var viewModel: DeviceSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Setup the initial variables
        macID = deviceSettingsFragmentArgs.macID
        viewModel = DeviceSettingsViewModel(macID)

        return inflater.inflate(R.layout.fragment_device_settings, container, false)
    }

}