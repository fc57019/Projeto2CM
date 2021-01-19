package com.example.projeto2cm.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.DISTANCE
import com.example.projeto2cm.activities.STEPS
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class HealthFragment : Fragment() {

    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_health, container, false)

        stepsView2 = view.findViewById(R.id.daily_steps_2)
        stepsView2?.text = STEPS.toString() + " Daily Steps"

        distanceView = view.findViewById(R.id.distance_view)
        distanceView?.text = DISTANCE.toString() + " Km"

        val circularProgressBar = view.findViewById<CircularProgressBar>(R.id.yourCircularProgressbar)
        circularProgressBar.apply {
            // or with animation
            STEPS?.let { setProgressWithAnimation(it.toFloat(), 1000) } // =1s

            // Set Progress Max
            progressMax = 200f

            // Set ProgressBar Color
            progressBarColor = Color.BLACK
            // or with gradient
            progressBarColorStart = Color.GRAY
            progressBarColorEnd = Color.RED
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set background ProgressBar Color
            backgroundProgressBarColor = Color.GRAY
            // or with gradient
            backgroundProgressBarColorStart = Color.WHITE
            backgroundProgressBarColorEnd = Color.RED
            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT

        }

        return view
    }

}