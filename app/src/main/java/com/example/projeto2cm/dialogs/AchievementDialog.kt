package com.example.projeto2cm.dialogs

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.AchievementFamilyAdapter
import com.example.projeto2cm.entities.Achievement
import com.example.projeto2cm.entities.ChatList
import com.example.projeto2cm.entities.User
import com.example.projeto2cm.fragments.mUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.time.LocalDateTime

var mUser2: List<User>? = null

class AchievementDialog : DialogFragment() {

    lateinit var radioGroup: RadioGroup
    lateinit var btn: Button

    private var firebaseUser: FirebaseUser? = null
    private var refUser: DatabaseReference? = null
    var refParticipant: DatabaseReference? = null

    lateinit var recyclerViewAchievementFamilyList: RecyclerView
    lateinit var achievementFamilyAdapter: AchievementFamilyAdapter
    private var usersChatList: List<ChatList>? = null

    lateinit var participanteID: String
    lateinit var participantName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_achievement_dialog, container, false)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)

        mUser = ArrayList()

        val cancel: Button = view.findViewById(R.id.cancel_btn1)
        cancel.setOnClickListener {
            dialog?.cancel()
        }

        LocalBroadcastManager.getInstance(context as Activity)
            .registerReceiver(mMessageReceiver, IntentFilter("participanteID"))

        radioGroup = view.findViewById(R.id.radioGroup1)
        btn = view.findViewById(R.id.apply_btn1)
        btn.setOnClickListener {
            var value = radioGroup.checkedRadioButtonId
            var rb = view.findViewById<RadioButton>(value)
            //Toast.makeText(context, rb.text.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(context, participanteID, Toast.LENGTH_LONG).show()

            var participante = participantName
            var timeStamp: String = LocalDateTime.now().toString()
            var timer: String = rb.text.toString()
            var tipoAchievement: String = "Amigavel"

            firebaseUser = FirebaseAuth.getInstance().currentUser
            refUser =
                FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

            var criador: String? = null
            var oldArray: ArrayList<Achievement>? = null



            refUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user: User? = snapshot.getValue(User::class.java)

                        criador = user!!.getName().toString()
                        oldArray = user.getAchievementList()!!
                        Log.e("oldArray e criador", " ${oldArray!!.size} / $criador  aaaaaaaaaaa")

                        var newAchievement =
                            Achievement(
                                timer!!,
                                participante,
                                criador!!,
                                timeStamp!!,
                                tipoAchievement!!,
                                "",
                                "",
                                "",
                                "",
                                firebaseUser!!.uid,
                                participanteID
                            )
                        oldArray!!.add(newAchievement)

                        val mapArray = HashMap<String, Any>()
                        mapArray["achievementList"] = oldArray!!
                        refUser?.updateChildren(mapArray)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            refParticipant = FirebaseDatabase.getInstance().reference
                .child("Users").child(participanteID)
            refParticipant!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(User::class.java)

                    var particip = user!!.getName().toString()
                    oldArray = user.getAchievementList()!!

                    var newAchievement =
                        Achievement(
                            timer!!,
                            particip,
                            criador!!,
                            timeStamp!!,
                            tipoAchievement!!,
                            "0",
                            "0",
                            "0",
                            "0",
                            firebaseUser!!.uid,
                            participanteID
                        )
                    oldArray!!.add(newAchievement)

                    val mapArray = HashMap<String, Any>()
                    mapArray["achievementList"] = oldArray!!
                    refParticipant?.updateChildren(mapArray)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }




        recyclerViewAchievementFamilyList = view.findViewById(R.id.achievement_family)
        recyclerViewAchievementFamilyList.setHasFixedSize(true)
        recyclerViewAchievementFamilyList.layoutManager = LinearLayoutManager(context)


        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()
        val ref =
            FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("antes do clear usersChatList", usersChatList!!.size.toString())
                (usersChatList as ArrayList).clear()
                Log.e("depois do clear usersChatList", usersChatList!!.size.toString())
                for (i in snapshot.children) {
                    val chatList = i.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }
                Log.e("for usersChatList", usersChatList!!.size.toString())
                getChatList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return view
    }

    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val participanteID1 = intent.getStringExtra("participanteID")
            val name1 = intent.getStringExtra("name")
            participanteID = participanteID1!!
            participantName = name1!!
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun getChatList() {
        (mUser as ArrayList<User>).clear()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<User>).clear()
                for (i in snapshot.children) {
                    val user = i.getValue(User::class.java)
                    for (eachChatList in usersChatList!!) {
                        if (user!!.getUID().equals(eachChatList.getId())) {
                            (mUser as ArrayList<User>).add(user!!)
                        }
                    }
                }
                achievementFamilyAdapter =
                    AchievementFamilyAdapter(context!!, mUser!!, true)
                recyclerViewAchievementFamilyList.adapter = achievementFamilyAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}