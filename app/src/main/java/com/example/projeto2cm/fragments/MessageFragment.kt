package com.example.projeto2cm.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.UserAdapter
import com.example.projeto2cm.adapters.UserAddAdapter
import com.example.projeto2cm.entities.ChatList
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageFragment : Fragment() {

    lateinit var recyclerViewChatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null
    private var userAdapter: UserAdapter? = null
    private var userAddAdapter: UserAddAdapter? = null
    private var mUser: List<User>? = null
    //private var userGroupChat: List<User>? = null
    private var usersChatList: List<ChatList>? = null
    private var recyclerView: RecyclerView? = null
    private var searchUserField: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        recyclerView = view.findViewById(R.id.search_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchUserField = view.findViewById(R.id.search_user_field)

        mUser = ArrayList()
        //getAllUsers()

        searchUserField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUser(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        recyclerViewChatList = view.findViewById(R.id.chat_groups)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (i in snapshot.children) {
                    val chatList = i.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }
                getChatList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return view
    }

    private fun searchForUser(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val queryUser =
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .orderByChild("searchUser")
                .startAt(str)
                .endAt(str + "\uf8ff")
        queryUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<User>).clear()
                if (searchUserField!!.text.toString() != "") {
                    for (i in snapshot.children) {
                        val user: User? = i.getValue(User::class.java)
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            (mUser as ArrayList<User>).add(user)
                            user.getEmail()?.let { Log.e("sdfgsf", it) }
                        }
                    }
                    userAddAdapter = UserAddAdapter(context!!, mUser!!, false)
                    recyclerView!!.adapter = userAddAdapter
                } else {
                    (mUser as ArrayList<User>).clear()
                    userAddAdapter = UserAddAdapter(context!!, mUser!!, false)
                    recyclerView!!.adapter = userAddAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
                        Log.e("erro user", "${user!!.getUID().equals(eachChatList.getId())}")
                        if (user!!.getUID().equals(eachChatList.getId()))
                            (mUser as ArrayList).add(user!!)
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