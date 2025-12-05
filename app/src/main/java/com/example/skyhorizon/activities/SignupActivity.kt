package com.example.skyhorizon.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skyhorizon.R
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnSignup = findViewById(R.id.btnSignup)

        btnSignup.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (!validateInput(email, password)) return@setOnClickListener

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Cont creat cu succes!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Eroare la crearea contului!", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            editEmail.error = "Introduceți un email"
            editEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Email invalid"
            editEmail.requestFocus()
            return false
        }
        if (password.length < 6) {
            editPassword.error = "Parola trebuie să aibă cel puțin 6 caractere"
            editPassword.requestFocus()
            return false
        }
        return true
    }
}