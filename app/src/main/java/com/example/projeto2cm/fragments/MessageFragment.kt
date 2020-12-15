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
import com.example.projeto2cm.User
import com.example.projeto2cm.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUser: List<User>? = null
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

        return view
    }

    private fun getAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val refUser = FirebaseDatabase.getInstance().reference.child("Users")
        refUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<User>).clear()
                if (searchUserField!!.text.toString() == "") {
                    for (i in snapshot.children) {
                        val user: User? = i.getValue(User::class.java)
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            (mUser as ArrayList<User>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUser!!, false)
                    recyclerView!!.adapter = userAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

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
                    userAdapter = UserAdapter(context!!, mUser!!, false)
                    recyclerView!!.adapter = userAdapter
                } else {
                    (mUser as ArrayList<User>).clear()
                    userAdapter = UserAdapter(context!!, mUser!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}