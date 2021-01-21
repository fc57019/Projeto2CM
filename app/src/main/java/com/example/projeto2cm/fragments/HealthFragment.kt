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
import com.mikhaellopez.circularprogressbar.CircularProgressBar

var progressMaxTemp: Float? = null

class HealthFragment : Fragment() {

    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_health, container, false)

        stepsView2 = view.findViewById(R.id.daily_steps_2)
        stepsView2?.text = STEPS.toString() + " Passos Diários"

        distanceView = view.findViewById(R.id.distance_view)
        distanceView?.text = DISTANCE.toString() + " Km"

        passosDados = view.findViewById(R.id.textView)

        // Set Progress Max
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val circularProgressBar =
                    view.findViewById<CircularProgressBar>(R.id.circularProgressBar)
                circularProgressBar.apply {
                    if (snapshot.exists()) {
                        val user: User? = snapshot.getValue(User::class.java)
                        val passos = user?.getSteps()
                        if (passos.equals("0") || passos.equals("6 000")) {
                            progressMax = 6000f
                            Log.e("Primeiro", "Primeiro")
                        }
                        if (passos.equals("10 000")) {
                            progressMax = 10000f
                            Log.e("Segundo", "Segundo")
                        }
                        if (passos.equals("12 000")) {
                            progressMax = 12000f
                            Log.e("Terceiro", "Terceiro")
                        }

                        progressMaxTemp = progressMax
                        passosDados?.text =
                            STEPS.toString() + " / " + progressMaxTemp!!.toInt()
                                .toString() + " Passos"
                    }
                    println(STEPS)
                    STEPS?.toFloat()?.let { setProgressWithAnimation(it, 1000) }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return view
    }

}