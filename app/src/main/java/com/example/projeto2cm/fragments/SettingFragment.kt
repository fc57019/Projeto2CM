package com.example.projeto2cm.fragments

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.example.projeto2cm.R
import java.util.*

val lang: String = "pt"
class SettingFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val addMemberToChat: CardView = view.findViewById(R.id.add_member_to_chat)
        addMemberToChat.setOnClickListener {
            addMemberToChatFun()
        }
        return view
    }

    private fun addMemberToChatFun() {
        MyCustomDialog().show(childFragmentManager, "MyCustomFragment")
    }
}

