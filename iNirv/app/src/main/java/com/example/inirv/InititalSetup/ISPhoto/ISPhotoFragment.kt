package com.example.inirv.InititalSetup.ISPhoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class ISPhotoFragment : Fragment() {

    companion object {
        fun newInstance() = ISPhotoFragment()
    }

    private lateinit var viewModel: ISPhotoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_is_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ISPhotoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}