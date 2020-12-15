package com.example.projeto2cm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projeto2cm.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingFragment : Fragment() {

    private lateinit var switch: SwitchMaterial


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        return view
    }
    
}