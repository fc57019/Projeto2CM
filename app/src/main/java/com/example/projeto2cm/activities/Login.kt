package com.example.projeto2cm.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnRegister: Button = findViewById(R.id.register_btn)
        btnRegister.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        val state: RelativeLayout = findViewById(R.id.login_visi)
        val btnLogin: Button = findViewById(R.id.login_btn)
        btnLogin.setOnClickListener {
            loginUser()
            state.visibility = View.VISIBLE

        }

    }

    private fun loginUser() {
        val emailLabelLog: String = findViewById<EditText>(R.id.email_login_label).text.toString()
        val passwordLabelLog: String =
            findViewById<EditText>(R.id.password_login_label).text.toString()

        if (emailLabelLog.isEmpty() && passwordLabelLog.isEmpty()) {
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_LONG).show()
        } else {
            firebaseAuth.signInWithEmailAndPassword(emailLabelLog, passwordLabelLog)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Erro" + task.exception?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            // verificar se o user ja está login
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}