package com.example.projeto2cm.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.UserAddAdapter
import com.example.projeto2cm.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyCustomDialog : DialogFragment() {

    var recyclerView: RecyclerView? = null
    private var searchUserField: EditText? = null
    private var userAddAdapter: UserAddAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_member_dialog, container, false)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)

        recyclerView = view.findViewById(R.id.search_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        searchUserField = view.findViewById(R.id.search_user_field)

        val cancel: Button = view.findViewById(R.id.cancel_btn)

        mUser = ArrayList()

        searchUserField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUser(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        cancel.setOnClickListener {
            dialog?.cancel()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                if (searchUserField!!.text.toString() != "" && str.contains(".com")) {  // meter regex se metermos outros logins
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


}