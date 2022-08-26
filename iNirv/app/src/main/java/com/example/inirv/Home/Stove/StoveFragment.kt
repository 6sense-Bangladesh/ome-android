package com.example.inirv.Home.Stove

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.inirv.Knob.HomeKnobFragment
import com.example.inirv.Knob.Knob
import com.example.inirv.R
import kotlinx.android.synthetic.main.fragment_stove.*

interface StoveFragmentDelegate{
    fun onStart()
    fun safetyLockPressed(isOn: Boolean, macID: String)
    fun turnOffPressed()
    fun goToScreen()
    fun getStoveOrientation(): Int
}

class StoveFragment: Fragment() {

    companion object {
        fun newInstance() = StoveFragment()
    }

    lateinit var viewModel: ViewModel
        private set
    lateinit var delegate: StoveFragmentDelegate
        private set

    private var homeKnobs: List<HomeKnobFragment> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (viewModel is StoveViewModel){
            val knobListObserver = Observer<List<Knob>> {

                this.addKnobsToHomeKnobViews()
            }

            (viewModel as StoveViewModel).knobs.observe(viewLifecycleOwner, knobListObserver)
            (viewModel as StoveViewModel).onStart()
        }

        return inflater.inflate(R.layout.fragment_stove, container, false)
    }

    fun updateKnob(macID: String){

    }

    private fun addKnobsToHomeKnobViews(){

        when((viewModel as StoveViewModel).userManager.user?.stoveOrientation){
            2 -> {

                homeKnobs = listOf(
                    homeknob3.getFragment(),
                    homeknob4.getFragment()
                )

                homeknob1.visibility = View.INVISIBLE
                homeknob2.visibility = View.INVISIBLE
                homeknob5.visibility = View.INVISIBLE
                homeknob6.visibility = View.INVISIBLE
                homeLinearLayout.visibility = View.VISIBLE
            }
            21 -> {

                homeKnobs = listOf(
                    homeknob8.getFragment(),
                    homeknob9.getFragment()
                )

                stoveTwoVerticalLayout.visibility = View.VISIBLE
            }
            4 -> {

                homeKnobs = listOf(
                    homeknob1.getFragment(),
                    homeknob2.getFragment(),
                    homeknob3.getFragment(),
                    homeknob4.getFragment()
                )

                homeLinearLayout.visibility = View.VISIBLE
                homeknob5.visibility = View.INVISIBLE
                homeknob6.visibility = View.INVISIBLE

            }
            5, 51 -> {

                homeKnobs = listOf(
                    homeknob1.getFragment(),
                    homeknob2.getFragment(),
                    homeknob5.getFragment(),
                    homeknob6.getFragment(),
                    homeknob7.getFragment()
                )

                homeknob3.visibility = View.INVISIBLE
                homeknob4.visibility = View.INVISIBLE
                homeLinearLayout.visibility = View.VISIBLE

            }
            6 -> {

                homeKnobs = listOf(
                    homeknob1.getFragment(),
                    homeknob2.getFragment(),
                    homeknob3.getFragment(),
                    homeknob4.getFragment(),
                    homeknob5.getFragment(),
                    homeknob6.getFragment()
                )

                homeLinearLayout.visibility = View.VISIBLE
            }
        }

        setupKnobs()
    }

    /**
     * Go through the home knob views and set them up with the appropriate knobs
     */
    private fun setupKnobs(){

        for (homeknob in homeKnobs){

            var macID = " "

            if ((viewModel as StoveViewModel).knobs.value != null){

                // Check to see if there is a knob at the current position for the home knob fragment
                // and set the macID appropriately
                (viewModel as StoveViewModel).getKnobAt(homeKnobs.indexOf(homeknob))?.let { grabbedKnob ->
                    macID = grabbedKnob.mMacID
                }

                homeknob.setup(
                    viewModel = (viewModel as StoveViewModel),
                    macID = macID
                )

            } else {
                homeknob.setup(
                    viewModel as StoveViewModel,
                    macID
                )
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = StoveViewModel(null)
        delegate = viewModel as StoveFragmentDelegate
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(StoveViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}