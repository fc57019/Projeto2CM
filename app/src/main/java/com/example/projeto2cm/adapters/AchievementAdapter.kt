package com.example.projeto2cm.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.AchievementActivity
import com.example.projeto2cm.entities.Achievement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

var b = Bundle()

class AchievementAdapter(
    private val context: Context,
    private val layout: Int,
    private val achievement: ArrayList<Achievement>
) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {
    class AchievementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeCriador: TextView = view.findViewById(R.id.criador)
        val nomeParticipante: TextView = view.findViewById(R.id.participante)
        val timer: TextView = view.findViewById(R.id.timer)
        val timeStamp: TextView = view.findViewById(R.id.time_stamp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        return AchievementViewHolder(
            LayoutInflater.from(context).inflate(layout, parent, false)
        )
    }

    override fun getItemCount() = achievement.size

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        var timeStamp = achievement[position].getTimeStamp().toString()
        val yourArray: List<String> = timeStamp.split("T")
        var date = yourArray[0]
        var hourT = yourArray[1]

        val yourArray2: List<String> = hourT.split(".")
        var hourT2 = yourArray2[0]

        val yourArray3: List<String> = hourT2.split(":")

        var hour = yourArray3[0] + ":" + yourArray3[1]



        holder.nomeCriador.text = achievement[position].getCriador().toString()
        holder.nomeParticipante.text = achievement[position].getParticipante()
        holder.timer.text = achievement[position].getTimer()
        holder.timeStamp.text = "$date / $hour"

        holder.itemView.setOnClickListener {
            //var temp = STEPS
            val intent = Intent(context, AchievementActivity::class.java)
            intent.putExtra("nomeCriador", achievement[position].getCriador().toString())
            intent.putExtra("nomeParticipante", achievement[position].getParticipante().toString())
            intent.putExtra("timer", achievement[position].getTimer().toString())
            intent.putExtra("timeStamp", "$date / $hour")

            var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            var currentUID = firebaseUser!!.uid


            Log.e(
                "currentUID == achievement[position].getCriadorID().toString()",
                (currentUID == achievement[position].getCriadorID().toString()).toString()
            )
            if (currentUID == achievement[position].getCriadorID().toString()) {
                var criadorPVez = achievement[position].getCPVez()!!
                b.putString("criadorP", criadorPVez)
                Log.e("8", "${criadorPVez}  criadorPVez")
            }

            Log.e(
                "currentUID == achievement[position].getFamiliarID().toString()",
                (currentUID == achievement[position].getFamiliarID().toString()).toString()
            )
            if (currentUID == achievement[position].getFamiliarID().toString()) {
                b.putString("t", achievement[position].getT())
                var familiarPVez = achievement[position].getFPVez()!!
                b.putString("familiarP", familiarPVez)
                Log.e("9", "${familiarPVez}  familiarPVez")
            }

            b.putString("position", position.toString())
            intent.putExtras(b)

            intent.putExtra(
                "tipoAchievement",
                achievement[position].getTipoAchievement().toString()
            )
            intent.putExtra("stepsCriador", achievement[position].getStepsCriador().toString())
            intent.putExtra("stepsFamiliar", achievement[position].getStepsFamiliar().toString())
            intent.putExtra("pointsCriador", achievement[position].getPointsCriador().toString())
            intent.putExtra("pointsFamiliar", achievement[position].getPointsFamiliar().toString())
            intent.putExtra("criadorID", achievement[position].getCriadorID().toString())
            intent.putExtra("familiarID", achievement[position].getFamiliarID().toString())


            Log.e("1", "${achievement[position].getCriador().toString()}  nomeCriador")
            Log.e("2", "${achievement[position].getParticipante().toString()}  nomeParticipante")
            Log.e("3", "${achievement[position].getTimer().toString()}  timer")
            Log.e("4", "$date / $hour  timeStamp")
            //Log.e("5", "$temp  startSteps")
            Log.e("6", "${achievement[position].getCriadorID().toString()}  criadorID")
            Log.e("7", "${achievement[position].getFamiliarID().toString()}  familiarID")
            context.startActivity(intent)


        }
    }
}