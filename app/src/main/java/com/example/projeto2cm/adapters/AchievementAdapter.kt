package com.example.projeto2cm.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.AchievementActivity
import com.example.projeto2cm.activities.STEPS
import com.example.projeto2cm.entities.Achievement

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

            var temp = STEPS
            val intent = Intent(context, AchievementActivity::class.java)
            intent.putExtra("nomeCriador", achievement[position].getCriador().toString())
            intent.putExtra("nomeParticipante", achievement[position].getParticipante().toString())
            intent.putExtra("timer", achievement[position].getTimer().toString())
            intent.putExtra("timeStamp", "$date / $hour")
            intent.putExtra("startSteps", temp)

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
            Log.e("5", "$temp  startSteps")
            Log.e("6", "${achievement[position].getCriadorID().toString()}  criadorID")
            Log.e("7", "${achievement[position].getFamiliarID().toString()}  familiarID")
            context.startActivity(intent)


        }
    }
}