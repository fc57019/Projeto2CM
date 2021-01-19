package com.example.projeto2cm.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.projeto2cm.R


class AchievementDialog : DialogFragment() {

    lateinit var radioGroup: RadioGroup
    lateinit var btn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_achievement_dialog, container, false)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)

        val cancel: Button = view.findViewById(R.id.cancel_btn1)
        cancel.setOnClickListener {
            dialog?.cancel()
        }

        radioGroup = view.findViewById(R.id.radioGroup1)
        btn = view.findViewById(R.id.apply_btn1)
        btn.setOnClickListener {
            var value = radioGroup.checkedRadioButtonId
            var rb = view.findViewById<RadioButton>(value)
            Toast.makeText(context, rb.text.toString(), Toast.LENGTH_SHORT).show()
            dialog?.cancel()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}