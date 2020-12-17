package com.example.projeto2cm.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.projeto2cm.R
import com.example.projeto2cm.fragments.*

class NavigationManager {

    companion object {
        private fun placeFragment(fm: FragmentManager, fragment: Fragment) {
            val transition = fm.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }

        fun goToHomeFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                HomeFragment()
            )
        }

        fun goToHealthFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                HealthFragment()
            )
        }

        fun goToProfileFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                ProfileFragment()
            )
        }

        fun goToSettingFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                SettingFragment()
            )
        }

        fun goToMessageFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                MessageFragment()
            )
        }


    }

}