package com.example.inirv.Knob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.inirv.Home.Stove.StoveViewModel
import com.example.inirv.R
import kotlinx.android.synthetic.main.fragment_home_knob.*

class HomeKnobFragment: Fragment() {

    companion object{

        fun newInstance(): HomeKnobFragment {

            return HomeKnobFragment()
        }
    }

    var macID: String = ""
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home_knob, container, false)
    }

    fun setup(viewModel: StoveViewModel, macID: String){

        this.macID = macID

        if (macID != " "){

        } else {

        }
    }

    fun rotateByAngle(angle: Double){
        this.homeFragKnobHand.rotation = angle.toFloat()
        this.homeFragKnobArrow.rotation = angle.toFloat()
    }
}