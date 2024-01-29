package com.ayush.bounte

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {

    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnSignup : TextView
    private lateinit var auth : FirebaseAuth
    private lateinit var btnLogin : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btn_login)
        btnSignup = findViewById(R.id.btn_signup)
        auth = FirebaseAuth.getInstance()


        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                login(email , password)
            }else{
                Toast.makeText(this, "Please enter Required fields", Toast.LENGTH_SHORT).show()
            }

        }

        btnSignup.setOnClickListener {

            startActivity(
                Intent (this , Register ::class.java)
            )

        }


    }

    fun login(email : String , password : String) {
        auth.signInWithEmailAndPassword(email , password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //open upload Product Screen
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this , BindingNav::class.java)  //test activity redirected
                    )

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()

                }
            }
    }



    }

