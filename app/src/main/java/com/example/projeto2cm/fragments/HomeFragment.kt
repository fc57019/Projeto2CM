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

var stepsView: TextView? = null

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
        stepsView?.text = STEPS.toString() + "Daily Steps"

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