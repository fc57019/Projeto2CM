package com.example.projeto2cm.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.DISTANCE
import com.example.projeto2cm.activities.STEPS
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


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

        return view
    }

}