package com.example.projeto2cm.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.b
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class AchievementActivity : AppCompatActivity() {

    private var START_TIME_IN_MILLIS: Long = 1800000
    private var mTextViewCountDown: TextView? = null
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0

    private var firebaseUser: FirebaseUser? = null
    private var refUser: DatabaseReference? = null
    private var refUserF: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement2)


        val logout: ImageButton = findViewById(R.id.logout3_btn)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SplashScreen::class.java))
            finish()
        }

        val btnBack: ImageButton = findViewById(R.id.back_btn)
        btnBack.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        // nosso
        val nomeCriador = intent.getStringExtra("nomeCriador")
        val nomeParticipante = intent.getStringExtra("nomeParticipante")
        val timer = intent.getStringExtra("timer")
        val timeStamp = intent.getStringExtra("timeStamp")
        val tipoAchievement = intent.getStringExtra("tipoAchievement")
        val stepsCriador = intent.getStringExtra("stepsCriador")
        val stepsFamiliar = intent.getStringExtra("stepsFamiliar")
        val pointsCriador = intent.getStringExtra("pointsCriador")
        val pointsFamiliar = intent.getStringExtra("pointsFamiliar")
        val criadorID = intent.getStringExtra("criadorID")
        val familiarID = intent.getStringExtra("familiarID")
        var criadorPVez = b.getString("criadorP")
        var familiarPVez = b.getString("familiarP")
        var position = b.getString("position")
        var t = b.getString("t")

        //XML
        var timerProg = findViewById<TextView>(R.id.timer_prog)
        var criadorCompeticao = findViewById<TextView>(R.id.criador_da_competicao_res)
        var criadorSteps = findViewById<TextView>(R.id.criador_steps_res)
        var criadorPoint = findViewById<TextView>(R.id.criador_point_res)
        var familyCompeticao = findViewById<TextView>(R.id.family_da_competicao_res)
        var familySteps = findViewById<TextView>(R.id.family_steps_res)
        var familyPoint = findViewById<TextView>(R.id.family_point_res)
        var timeStampT = findViewById<TextView>(R.id.time_stamp_prog)
        var stop = findViewById<Button>(R.id.stop_btn)

        mTextViewCountDown = findViewById(R.id.progress_countdown)
        val strs = timer!!.split(" ").toTypedArray()
        if (strs[0].equals("30")) {
            START_TIME_IN_MILLIS = 1800000
        } else if (strs[0].equals("60")) {
            START_TIME_IN_MILLIS = 3600000
        } else {
            START_TIME_IN_MILLIS = 5400000
        }
        mTimeLeftInMillis = START_TIME_IN_MILLIS
        startTimer()
        updateCountDownText()

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        if (prefs.getBoolean("test", true) != null) {
            Log.e("ifffff", "ifffffffff")
            prefs.edit().putBoolean("test", false).apply()

            mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS)
            mTimerRunning = prefs.getBoolean("timerRunning", false)
            updateCountDownText()
            if (mTimerRunning) {
                mEndTime = prefs.getLong("endTime", 0)
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis()
                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0
                    mTimerRunning = false
                    updateCountDownText()
                } else {
                    startTimer()
                }
            }
        }

        Log.e("STEPOS", "$STEPS  sdsdsds")
        Log.e("STEPOS2", "$criadorID  criadorID")
        Log.e("STEPOS3", "$familiarID  familiarID")
        Log.e("STEPOS4", "$nomeCriador  nomeCriador")
        Log.e("STEPOS5", "$nomeParticipante  nomeParticipante")
        Log.e("STEPOS6", criadorPVez.toString() + "criadorPVez")
        Log.e("STEPOS7", familiarPVez.toString() + "familiarPVez")

        timerProg.text = "Duração " + timer
        criadorCompeticao.text = nomeCriador
        familyCompeticao.text = nomeParticipante
        timeStampT.text = "Criado a " + timeStamp


        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser =
            FirebaseDatabase.getInstance().reference.child("Users").child(criadorID.toString())
        refUserF =
            FirebaseDatabase.getInstance().reference.child("Users").child(familiarID.toString())
        var currentUID = firebaseUser!!.uid


        if (criadorPVez == "1" && currentUID == criadorID) {
            stop.text = "Parar"
        } else if (criadorPVez != "1" && currentUID == criadorID) {
            stop.text = "Resultado"
        } else if (familiarPVez == "1" && currentUID == familiarID) {
            stop.text = "Parar"
        } else if (familiarPVez != "1" && currentUID == familiarID) {
            stop.text = "Resultado"
        }

        stop.setOnClickListener {

            if (currentUID == criadorID) {

                if (criadorPVez == "1") {
                    Log.e("STEPOsssssssS", "$STEPS  sdsdsds")
                    var passosDadosFinais = STEPS
                    var pontosfinais = passosDadosFinais!!.toInt() * 6

                    refUser =
                        FirebaseDatabase.getInstance().reference.child("Users").child(criadorID)
                    refUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user: User? = snapshot.getValue(User::class.java)
                                var arrayAchie = user!!.getAchievementList()

                                criadorSteps.text = passosDadosFinais.toString()
                                criadorPoint.text = pontosfinais.toString()
                                arrayAchie!![position!!.toInt()].setPointsCriador(pontosfinais.toString())
                                arrayAchie[position!!.toInt()].setStepsCriador(passosDadosFinais.toString())
                                arrayAchie[position!!.toInt()].setCPVez((20).toString())

                                val mapArray = HashMap<String, Any>()
                                mapArray["achievementList"] = arrayAchie
                                refUser!!.updateChildren(mapArray)


                                refUserF = FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(familiarID.toString())
                                refUserF!!.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val user: User? = snapshot.getValue(User::class.java)
                                            var arrayAchie1 = user!!.getAchievementList()
                                            Log.e(
                                                "tamanho array",
                                                arrayAchie1!!.size.toString() + "tamanho array"
                                            )

                                            for (i in arrayAchie1.indices) {
                                                if (arrayAchie1[i].getTimeStamp() == arrayAchie[position!!.toInt()].getTimeStamp()) {
                                                    arrayAchie1[i].setStepsCriador(passosDadosFinais.toString()) // passosDados
                                                    arrayAchie1[i].setPointsCriador(pontosfinais.toString()) //pontosfinais
                                                }
                                            }

                                            val mapArray = HashMap<String, Any>()
                                            mapArray["achievementList"] = arrayAchie1
                                            refUserF!!.updateChildren(mapArray)
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                    stop.text = "Resultado"
                } else {
                    Log.e("else criador", "else criador")
                    refUser =
                        FirebaseDatabase.getInstance().reference.child("Users").child(criadorID!!)
                    refUser!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user: User? = snapshot.getValue(User::class.java)
                                var arrayAchie = user!!.getAchievementList()

                                runOnUiThread {
                                    criadorSteps.text =
                                        arrayAchie!![position!!.toInt()].getStepsCriador()
                                    criadorPoint.text =
                                        arrayAchie[position!!.toInt()].getPointsCriador()
                                    Log.e("a", criadorSteps.text.toString())
                                    Log.e("b", criadorPoint.text.toString())

                                    familySteps.text =
                                        arrayAchie[position!!.toInt()].getStepsFamiliar()
                                    familyPoint.text =
                                        arrayAchie[position!!.toInt()].getPointsFamiliar()

                                    Log.e(
                                        "c",
                                        arrayAchie[position!!.toInt()].getStepsFamiliar()
                                            .toString() + "11111111111"
                                    )
                                    Log.e(
                                        "d",
                                        arrayAchie[position!!.toInt()].getPointsFamiliar()
                                            .toString() + "2222222222222"
                                    )

                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }

            if (currentUID == familiarID) {
                if (familiarPVez == "1") {
                    var finalSteps = STEPS
                    var passosDadosFinais = STEPS
                    var pontosfinais = passosDadosFinais!!.toInt() * 6

                    refUserF =
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(familiarID.toString())
                    refUserF!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user: User? = snapshot.getValue(User::class.java)
                                var arrayAchie3 = user!!.getAchievementList()

                                familySteps.text = passosDadosFinais.toString()
                                familyPoint.text = pontosfinais.toString()
                                arrayAchie3!![position!!.toInt()].setPointsFamiliar(pontosfinais.toString())
                                arrayAchie3[position!!.toInt()].setStepsFamiliar(passosDadosFinais.toString())
                                arrayAchie3[position.toInt()].setT(2.toString())

                                if (arrayAchie3[position!!.toInt()].getFPVez() == 1.toString()) {
                                    arrayAchie3[position!!.toInt()].setFPVez(20.toString())
                                }


                                val mapArray = HashMap<String, Any>()
                                mapArray["achievementList"] = arrayAchie3
                                refUserF!!.updateChildren(mapArray)

                                refUser =
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(criadorID.toString())
                                refUser!!.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val user: User? =
                                                snapshot.getValue(User::class.java)
                                            var arrayAchie4 = user!!.getAchievementList()

                                            for (i in arrayAchie4!!.indices) {
                                                if (arrayAchie4[i].getTimeStamp() == arrayAchie3[position!!.toInt()].getTimeStamp()) {
                                                    arrayAchie4[i].setStepsFamiliar(
                                                        passosDadosFinais.toString()
                                                    ) // passosDados
                                                    arrayAchie4[i].setPointsFamiliar(pontosfinais.toString()) //pontosfinais
                                                }
                                            }

                                            val mapArray = HashMap<String, Any>()
                                            mapArray["achievementList"] = arrayAchie4
                                            refUserF!!.updateChildren(mapArray)
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                    stop.text = "Resultado"
                } else {
                    Log.e("else familiar", "else familiar")
                    refUserF =
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(familiarID.toString())
                    refUserF!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user: User? = snapshot.getValue(User::class.java)
                                var arrayAchie5 = user!!.getAchievementList()
                                Log.e(
                                    "tamanho array",
                                    arrayAchie5!!.size.toString() + "tamanho array"
                                )

                                runOnUiThread {
                                    criadorSteps.text =
                                        arrayAchie5[position!!.toInt()].getStepsCriador()
                                    criadorPoint.text =
                                        arrayAchie5[position!!.toInt()].getPointsCriador()
                                    familySteps.text =
                                        arrayAchie5[position!!.toInt()].getStepsFamiliar()
                                    familyPoint.text =
                                        arrayAchie5[position!!.toInt()].getPointsFamiliar()
                                }


                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
            }
        }.start()
        mTimerRunning = true
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted =
            java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        mTextViewCountDown!!.text = timeLeftFormatted
    }

    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean("test", true)
        editor.putLong("millisLeft", mTimeLeftInMillis)
        editor.putBoolean("timerRunning", mTimerRunning)
        editor.putLong("endTime", mEndTime)
        editor.apply()
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong("millisLeft", mTimeLeftInMillis)
        editor.putBoolean("test", true)
        editor.putBoolean("timerRunning", mTimerRunning)
        editor.putLong("endTime", mEndTime)
        editor.apply()
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS)
        mTimerRunning = prefs.getBoolean("timerRunning", false)
        updateCountDownText()
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0)
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis()
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0
                mTimerRunning = false
                updateCountDownText()
            } else {
                startTimer()
            }
        }
    }

}