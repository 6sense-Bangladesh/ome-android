package com.example.inirv.Home.Settings.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inirv.Knob.Knob
import com.example.inirv.R

interface SettingsFragmentDelegate{

    fun getKnobs(): List<Knob>
}

class SettingsFragment : Fragment(), MyDevicesCardViewAdapterDelegate {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel
    private var buttonClicked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        // Reset action vars
        buttonClicked = false

        // Setup knob list
        this.activity?.findViewById<RecyclerView>(R.id.settings_fragment_recycler_view_my_devices)?.let { recyclerView ->

            recyclerView.layoutManager = LinearLayoutManager(this.context)

            val adapter = MyDevicesCardViewAdapter(viewModel.getKnobs(), this)
            recyclerView.adapter = adapter
        }

        // Setup on click listeners

        // Setup a new device
        activity?.findViewById<TableRow>(R.id.settings_fragment_table_row_add_new_knob)?.let{ addNewKnobRow ->

            addNewKnobRow.setOnClickListener {

                if(wasButtonClicked()){
                    return@setOnClickListener
                }
            }
        }

        // Auto shutoff
        activity?.findViewById<TableRow>(R.id.settings_fragment_table_row_auto_shut_off)?.let{ autoShutOffRow ->

            autoShutOffRow.setOnClickListener {

                if (wasButtonClicked()){
                    return@setOnClickListener
                }

                val action = SettingsFragmentDirections.actionSettingsFragmentToAutoShutoff()
                val localNavHost = requireParentFragment().findNavController()

                localNavHost.navigate(action)
            }
        }

    }

    fun wasButtonClicked(): Boolean{

        if (buttonClicked){
            return buttonClicked
        } else {
            buttonClicked = true
            return false
        }
    }

    override fun mdcvadOnClick(macID: String) {

        if (wasButtonClicked()){
            return
        }

        val action = SettingsFragmentDirections.actionSettingsFragmentToDeviceSettings(macID)
        val localNavHost = requireParentFragment().findNavController()

        localNavHost.navigate(action)
    }

}