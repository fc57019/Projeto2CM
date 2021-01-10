package com.example.projeto2cm.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.UserAdapter
import com.example.projeto2cm.entities.ChatList
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

var mUser: List<User>? = null

class MessageFragment : Fragment() {

    lateinit var recyclerViewChatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null
    private var userAdapter: UserAdapter? = null
    private var usersChatList: List<ChatList>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        mUser = ArrayList()
        //getAllUsers()

        recyclerViewChatList = view.findViewById(R.id.chat_groups)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)

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

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                delete(viewHolder.adapterPosition)
                userAdapter?.notifyItemChanged(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerViewChatList)

        return view
    }

    private fun delete(x: Int) {
        Log.e("ollool", "jjghhgjhjg")
    }


    private fun getChatList() {
        mUser = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList).clear()
                for (i in snapshot.children) {
                    val user = i.getValue(User::class.java)
                    for (eachChatList in usersChatList!!) {
                        if (user!!.getUID().equals(eachChatList.getId())) {
                            (mUser as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUser as ArrayList<User>), true)
                recyclerViewChatList.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}