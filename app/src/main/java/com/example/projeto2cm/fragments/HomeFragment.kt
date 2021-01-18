package com.example.projeto2cm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.STEPS
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.sql.SQLOutput
import java.util.*


var stepsView: TextView? = null
var teste: Int = 1

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var info: ImageView? = view?.findViewById<ImageView>(R.id.info_achievement)
        info?.setOnClickListener {
            openInfo()
        }

        stepsView = view.findViewById(R.id.daily_steps)
        var refUser: DatabaseReference? = null
        var firebaseUser: FirebaseUser? = null
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)
                    val cal: Calendar = Calendar.getInstance()
                    val currentDay: Int = cal.get(Calendar.DAY_OF_MONTH)
                    print("diua base de dados "+user?.getDia())
                    println("dia hj "+currentDay)
                    if (user?.getDia() != currentDay) {
                        println("dia diferente 00000000000")
                        val mapDia = HashMap<String, Any>()
                        mapDia["dia"] = currentDay
                        refUser?.updateChildren(mapDia)
                        teste=0
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        if(teste==0){
            println("RESETTTTTTTTTTTTT")
            stepsView?.text = "0 Daily Steps"
        }else{
            println("NOT RESETTTTTTTTTTTTTTTTT")
            stepsView?.text = STEPS.toString() + "Daily Steps"
        }


        return view
    }

    fun openInfo() {
        var sistemaDePontosPorCompetição: TextView? =
            view?.findViewById<TextView>(R.id.Sistema_de_pontos_por_competição)
        var sistemaDePontosPorCompetiçãoCardview: CardView? =
            view?.findViewById<CardView>(R.id.Sistema_de_pontos_por_competição_cardview)
        var sistemaDePontosPorPassos: TextView? =
            view?.findViewById<TextView>(R.id.sistema_de_pontos_por_passos)
        var sistemaDePontosPorPassosCardview: CardView? =
            view?.findViewById<CardView>(R.id.sistema_de_pontos_por_passos_cardview)

        if (sistemaDePontosPorCompetição?.visibility == View.VISIBLE) {
            sistemaDePontosPorCompetição?.visibility = View.GONE
            sistemaDePontosPorCompetiçãoCardview?.visibility = View.GONE
            sistemaDePontosPorPassos?.visibility = View.GONE
            sistemaDePontosPorPassosCardview?.visibility = View.GONE
        } else {
            sistemaDePontosPorCompetição?.visibility = View.VISIBLE
            sistemaDePontosPorCompetiçãoCardview?.visibility = View.VISIBLE
            sistemaDePontosPorPassos?.visibility = View.VISIBLE
            sistemaDePontosPorPassosCardview?.visibility = View.VISIBLE
        }
    }
}

