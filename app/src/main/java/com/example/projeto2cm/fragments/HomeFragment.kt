package com.example.projeto2cm.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.STEPS
import com.example.projeto2cm.adapters.AchievementAdapter
import com.example.projeto2cm.entities.Achievement
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


var stepsView: TextView? = null
var pointsView: TextView? = null
var teste: Int = 1
var stepsView2: TextView? = null
var distanceView: TextView? = null
var ALTURA: String? = ""
var passosDados: TextView? = null

class HomeFragment : Fragment() {

    lateinit var recyclerViewAchievement: RecyclerView
    lateinit var achievementAdapter: AchievementAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var info: ImageView? = view?.findViewById<ImageView>(R.id.info_achievement)
        info?.setOnClickListener {
            openInfo()
        }

        recyclerViewAchievement = view.findViewById(R.id.achievements)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerViewAchievement.layoutManager = linearLayoutManager

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        var newArray = ArrayList<Achievement>()
        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)
                    var oldArray = user!!.getAchievementList()
                    newArray = oldArray!!
                    recyclerViewAchievement.layoutManager = LinearLayoutManager(context)
                    achievementAdapter = AchievementAdapter(
                        activity as Context,
                        R.layout.achievement_card,
                        oldArray
                    )
                    recyclerViewAchievement.adapter = achievementAdapter
                    Log.e("new array", "new array size ${newArray.size}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




        stepsView = view.findViewById(R.id.daily_steps)
        stepsView?.text = STEPS.toString() + " Passos Diários"

        pointsView = view.findViewById(R.id.pontos)
        pointsView?.text = (STEPS!!.toInt() * 2).toString() + " Pontos Diários"

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

