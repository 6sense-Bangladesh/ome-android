package com.example.inirv.InititalSetup.ISOrientation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class ISOrientationFragment : Fragment() {

    companion object {
        fun newInstance() = ISOrientationFragment()
    }

    private lateinit var viewModel: ISOrientationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_is_orientation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ISOrientationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}