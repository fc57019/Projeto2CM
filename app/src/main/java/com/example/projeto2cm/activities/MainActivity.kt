package com.example.projeto2cm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.example.projeto2cm.utils.NavigationManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    @SuppressLint("RestrictedApi", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (!screenRotated(savedInstanceState)) {
            NavigationManager.goToHomeFragment(supportFragmentManager)
        }


        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.ic_health -> {
                        NavigationManager.goToHealthFragment(supportFragmentManager)
                    }
                    R.id.ic_messages -> {
                        NavigationManager.goToMessageFragment(supportFragmentManager)
                    }
                    R.id.ic_home -> {
                        NavigationManager.goToHomeFragment(supportFragmentManager)
                    }
                    R.id.ic_profile -> {
                        NavigationManager.goToProfileFragment(supportFragmentManager)
                    }
                    R.id.ic_settings -> {
                        NavigationManager.goToSettingFragment(supportFragmentManager)
                    }
                }
                true
            }

        val logout: ImageButton = findViewById(R.id.logout_btn)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SplashScreen::class.java))
        }
    }

    private fun screenRotated(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState != null
    }

}