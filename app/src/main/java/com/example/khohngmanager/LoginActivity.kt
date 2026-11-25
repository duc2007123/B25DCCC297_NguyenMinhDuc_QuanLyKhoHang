package com.example.khohngmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        tvError = findViewById(R.id.tvError)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            checkLogin()
        }
    }

    private fun checkLogin() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            tvError.text = "Tên đăng nhập và mật khẩu không được để trống"
            tvError.visibility = View.VISIBLE
            return
        }

        val isValid = dbHelper.checkLogin(username, password)
        if (isValid) {
            tvError.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            tvError.text = "Sai tên đăng nhập hoặc mật khẩu"
            tvError.visibility = View.VISIBLE
        }
    }
}
