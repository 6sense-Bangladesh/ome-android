package com.example.inirv.Home.HomeTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.inirv.R
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StoveTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoveTabFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    var stoveBottomNavigationBar: BottomNavigationView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stove_tab, container, false)
    }

    override fun onStart() {
        super.onStart()

        stoveBottomNavigationBar = this.view?.findViewById(R.id.stove_bottom_navigation_bar)
        stoveBottomNavigationBar?.selectedItemId = R.id.stove_tab_action_my_stove
        val stoveTabNavHost: View? =  this.view?.findViewById(R.id.stove_tab_nav_host_fragment)


        if (stoveTabNavHost != null) {
            stoveBottomNavigationBar?.setupWithNavController(stoveTabNavHost.findNavController())
        }

    }

//    private fun setCurrentFragment(fragment: Fragment) =
//        activity?.supportFragmentManager?.beginTransaction()?.apply {
//            replace(R.id.stove_tab_nav_host_fragment, fragment)
//            commit()
//        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StoveTabFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            StoveTabFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }

        fun newInstance() = StoveTabFragment()
    }
}