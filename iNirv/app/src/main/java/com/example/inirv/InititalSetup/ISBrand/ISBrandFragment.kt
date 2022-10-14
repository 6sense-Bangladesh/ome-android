package com.example.inirv.InititalSetup.ISBrand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class ISBrandFragment : Fragment() {

    companion object {
        fun newInstance() = ISBrandFragment()
    }

    private lateinit var viewModel: ISBrandViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_is_brand, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ISBrandViewModel::class.java)
        // TODO: Use the ViewModel
    }

}