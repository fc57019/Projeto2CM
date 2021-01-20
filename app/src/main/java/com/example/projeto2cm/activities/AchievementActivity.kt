package com.example.projeto2cm.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.example.projeto2cm.entities.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AchievementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement2)

        // nosso
        val nomeCriador = intent.getStringExtra("nomeCriador")
        val nomeParticipante = intent.getStringExtra("nomeParticipante")
        val timer = intent.getStringExtra("timer")
        val timeStamp = intent.getStringExtra("timeStamp")
        val startSteps = intent.getStringExtra("startSteps")

        val tipoAchievement = intent.getStringExtra("tipoAchievement")
        val stepsCriador = intent.getStringExtra("stepsCriador")
        val stepsFamiliar = intent.getStringExtra("stepsFamiliar")
        val pointsCriador = intent.getStringExtra("pointsCriador")
        val pointsFamiliar = intent.getStringExtra("pointsFamiliar")
        val criadorID = intent.getStringExtra("criadorID")
        val familiarID = intent.getStringExtra("familiarID")

        var timerProg = findViewById<TextView>(R.id.timer_prog)
        var criadorCompeticao = findViewById<TextView>(R.id.criador_da_competicao_res)
        var criadorSteps = findViewById<TextView>(R.id.criador_steps_res)
        var criadorPoint = findViewById<TextView>(R.id.criador_point_res)
        var familyCompeticao = findViewById<TextView>(R.id.family_da_competicao_res)
        var familySteps = findViewById<TextView>(R.id.family_steps_res)
        var familyPoint = findViewById<TextView>(R.id.family_point_res)
        var timeStampT = findViewById<TextView>(R.id.time_stamp_prog)

        timerProg.text = timer
        criadorCompeticao.text = nomeCriador
        familyCompeticao.text = nomeParticipante
        timeStampT.text = timeStamp

        Log.e("STEPOS", "$STEPS  sdsdsds")
        Log.e("STEPOS1", "$startSteps  111111111")
        Log.e("STEPOS2", "$criadorID  criadorID")
        Log.e("STEPOS3", "$familiarID  familiarID")
        Log.e("STEPOS4", "$nomeCriador  nomeCriador")
        Log.e("STEPOS5", "$nomeParticipante  nomeParticipante")

        var temps = "90 Min"
        if (timer == temps) {
            var finalSteps = STEPS
            var totalAchievementPoints = 0 //(finalSteps!!.toInt() - startSteps!!.toInt()) * 6
            criadorPoint.text = "0" //totalAchievementPoints.toString()
            criadorSteps.text = "0"  //(finalSteps!!.toInt() - startSteps!!.toInt()).toString()


            val ref =
                FirebaseDatabase.getInstance().reference.child("Users").child(criadorID!!)
            ref!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(User::class.java)

                    var arrayAchie = user!!.getAchievementList()

                    for (i in arrayAchie!!) {
                        if (i.getTimeStamp() == timeStamp) {
                            i.setPointsCriador(totalAchievementPoints.toString())
                            i.setStepsCriador(criadorSteps.text.toString())
                        }
                    }

                    val mapArray = HashMap<String, Any>()
                    mapArray["achievementList"] = arrayAchie
                    ref?.updateChildren(mapArray)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            temps = "1"
        }


        val refParti =
            FirebaseDatabase.getInstance().reference.child("Users").child(familiarID.toString())
        refParti!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)

                var arrayAchie = user!!.getAchievementList()

                for (i in arrayAchie!!) {
                    if (i.getTimeStamp() == timeStamp) {
                        var familyPoint1 = i.getPointsFamiliar()
                        var familySteps1 = i.getStepsFamiliar()
                        if (!(familyPoint1.isNullOrEmpty()) || !(familySteps1.isNullOrEmpty())) {
                            familySteps.text = familySteps1
                            familyPoint.text = familyPoint1
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}