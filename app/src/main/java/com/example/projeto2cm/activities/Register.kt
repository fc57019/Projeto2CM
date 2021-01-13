package com.example.projeto2cm.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        val state: RelativeLayout = findViewById(R.id.regist_visi)
        val registoCompletoBtn: Button = findViewById(R.id.register_complete_btn)
        registoCompletoBtn.setOnClickListener {
            registUser()
            state.visibility = View.VISIBLE
        }

        val registoGoogle: ImageView = findViewById(R.id.regist_google)
        registoGoogle.setOnClickListener {
            registoGoogleUser()
        }
    }

    private fun registoGoogleUser() {
        TODO("Not yet implemented")
    }

    private fun registUser() {
        val nameLabel: String = findViewById<EditText>(R.id.name_regist_label).text.toString()
        val emailLabel: String = findViewById<EditText>(R.id.email_regist_label).text.toString()
        val passwordLabel: String =
            findViewById<EditText>(R.id.password_regist_label).text.toString()
        val password2Label: String =
            findViewById<EditText>(R.id.password_2_regist_label).text.toString()

        if (nameLabel.isEmpty() || emailLabel.isEmpty()) {
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_LONG).show()
        } else if (passwordLabel.isEmpty() || password2Label.isEmpty() || passwordLabel != password2Label) {
            Toast.makeText(this, "Passwords diferentes", Toast.LENGTH_LONG).show()
        } else {
            firebaseAuth.createUserWithEmailAndPassword(emailLabel, passwordLabel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUserId = firebaseAuth.currentUser?.uid.toString()
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                            .child(firebaseUserId)
                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserId
                        userHashMap["name"] = nameLabel
                        userHashMap["profile"] =
                            "https://firebasestorage.googleapis.com/v0/b/projeto2cm.appspot.com/o/profile_holder.jpg?alt=media&token=8b577648-dfc8-488c-9d83-90601b96d2ba"
                        userHashMap["steps"] = "0"
                        userHashMap["genre"] = "genero"
                        userHashMap["date"] = "09/12/2020"
                        userHashMap["weight"] = "0.0"
                        userHashMap["height"] = "0.0"
                        userHashMap["searchUser"] = emailLabel.toLowerCase()

                        refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Erro " + task.exception?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}