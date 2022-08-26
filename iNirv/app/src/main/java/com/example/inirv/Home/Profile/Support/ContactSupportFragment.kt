package com.example.inirv.Home.Profile.Support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.inirv.R

class ContactSupportFragment : Fragment() {

    companion object {
        fun newInstance() = ContactSupportFragment()
    }

    private lateinit var viewModel: ContactSupportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_support, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactSupportViewModel::class.java)
        // TODO: Use the ViewModel
    }

}