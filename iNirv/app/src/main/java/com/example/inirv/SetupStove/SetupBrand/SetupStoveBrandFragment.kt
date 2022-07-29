package com.example.inirv.SetupStove.SetupBrand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class SetupStoveBrandFragment : Fragment() {

    companion object {
        fun newInstance() = SetupStoveBrandFragment()
    }

    private lateinit var viewModel: SetupStoveBrandViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_stove_brand, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SetupStoveBrandViewModel::class.java)
        // TODO: Use the ViewModel
    }

}