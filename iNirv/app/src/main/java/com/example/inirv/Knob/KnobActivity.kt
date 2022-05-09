package com.example.inirv.Knob

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.inirv.HomeActivity
import com.example.inirv.R
import kotlinx.android.synthetic.main.activity_knob.*
import java.util.*
import kotlin.math.atan2

class KnobActivity: AppCompatActivity()  {

    // Variables
    private var backButtonPressed: Boolean = false
    private var mCurrAngle: Double = 0.0
    private var mPrevAngle: Double = 0.0
    var knob: Knob? = null;
    var mAngle: Int? = null;


    var angleInRadians: Float? = null
    var double: Double = 0.0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the appropriate activity
        setContentView(R.layout.activity_knob)

        // Set the appropriate variables for the
        mCurrAngle = intent.getIntExtra("angle", 0).toDouble()

        // TODO: Remove when done testing out the rotations
        var listener = View.OnTouchListener { view, motionEvent ->

            // Get the center x and y of the layout that holds the arrow layout
            val xC = knobRotateLayout.width / 2
            val yC = knobRotateLayout.height / 2

            // Get the fingers x and y
            val x = motionEvent.getX().toDouble()
            val y = motionEvent.getY().toDouble()


            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    knobRotateLayout.clearAnimation()
                    mCurrAngle = Math.toDegrees(atan2(x - xC, yC - y))
                }
                MotionEvent.ACTION_MOVE -> {
                    mPrevAngle = mCurrAngle
                    mCurrAngle = Math.toDegrees(atan2(x - xC, yC - y))
                    animate(mPrevAngle, mCurrAngle, 0, "knobRotateLayout");
                }
                MotionEvent.ACTION_UP -> {
                    val prevAngle = knobActivityKnobHand.rotation.toDouble()
                    animate(prevAngle, mCurrAngle, 500, "knobActivityKnobHand");

                }
                else -> {
                    print("Some other Action")
                }

            }

            true

        }

        knobRotateLayout.setOnTouchListener(listener)
    }

    override fun onResume() {
        super.onResume()

        // Set the button booleans
        backButtonPressed = false
    }

    // Back button action
    fun knobActivityBackButtonPressed(view: View){

        // Check if the button was pressed
        if (backButtonPressed){
            return
        }

        //  Set the button pressed variable
        backButtonPressed = true

        val intent = Intent()
        intent.putExtra("knobActivityAngle", mCurrAngle)
        setResult(Activity.RESULT_OK, intent)
        Log.d("Activity Result", mCurrAngle.toString())

        // Run the back button pressed function
        onBackPressed()
    }

    // Rotate the arrow to follow the users finger drag
    private fun animate(fromDeg: Double, toDeg: Double, durationMilis: Long, view: String) {
        val rotateAnimation = RotateAnimation(fromDeg.toFloat(), toDeg.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5F,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F)

        rotateAnimation.duration = durationMilis
        rotateAnimation.isFillEnabled = true
        rotateAnimation.fillAfter = true

        // Check which object should be rotated currently
        when(view){
            "knobRotateLayout" -> {
                knobRotateLayout.startAnimation(rotateAnimation)
            }
            "knobActivityKnobHand" -> {
                knobActivityKnobHand.startAnimation(rotateAnimation)
            }
        }

        Log.d("KnobRotate", mCurrAngle.toString())
    }
}