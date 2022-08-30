package com.example.inirv.Home.Settings.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inirv.Knob.Knob
import com.example.inirv.R

interface SettingsFragmentDelegate{

    fun getKnobs(): List<Knob>
}

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

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

//        this.activity?.findViewById<RecyclerView>(R.id.settings_fragment_recycler_view_settings)?.let { settingsRecyclerView ->
//
//            settingsRecyclerView.layoutManager = LinearLayoutManager(this.context)
//        }

        this.activity?.findViewById<RecyclerView>(R.id.settings_fragment_recycler_view_my_devices)?.let { recyclerView ->

            recyclerView.layoutManager = LinearLayoutManager(this.context)

            val adapter = MyDevicesCardViewAdapter(viewModel.getKnobs())
            recyclerView.adapter = adapter
        }

    }

}