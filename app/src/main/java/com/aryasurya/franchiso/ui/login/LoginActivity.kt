package com.aryasurya.franchiso.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aryasurya.franchiso.MainActivity
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.User
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.databinding.ActivityLoginBinding
import com.aryasurya.franchiso.ui.register.RegisterActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val userId = viewModel.getCurrentUser()

        if (userId != null) {
            viewModel.fetchUserData(userId.uid)
        }

        binding.tvNotHaveAcc.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        validEmail()
        activeButton()
    }

    private fun activeButton() {
        val email = binding.tlEmail
        val password = binding.tlPassword

        binding.btnLogin.setOnClickListener {
            val inputEmail = email.editText?.text.toString()
            val inputPassword = password.editText?.text.toString()

            if (isEmailValid(inputEmail) && isPasswordValid(inputPassword)) {
                binding.overlayLoading.visibility = View.VISIBLE

                viewModel.loginUser(inputEmail, inputPassword)
                    .addOnCompleteListener { task ->
                        binding.overlayLoading.visibility = View.GONE // Sembunyikan ProgressBar

                        if (task.isSuccessful) {
                            val firebaseUser = viewModel.getCurrentUser() // Ambil pengguna dari hasil autentikasi Firebase
                            if (firebaseUser != null) {
                                // Ambil data pengguna dari Firestore
                                val db = FirebaseFirestore.getInstance()
                                val userDocument = db.collection("users").document(firebaseUser.uid)
                                userDocument.get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            val user = documentSnapshot.toObject(User::class.java)
                                            if (user != null) {
                                                // Simpan sesi pengguna setelah login berhasil
                                                val loggedInUser = User(
                                                    user.userId,
                                                    user.username,
                                                    user.email,
                                                    user.name,
                                                    user.photoProfileUrl,
                                                    user.role,
                                                    user.gender,
                                                    user.noTel
                                                )
                                                val sessionManager = SessionManager(this)
                                                sessionManager.saveSession(loggedInUser)

                                                // Lakukan tindakan setelah login berhasil
                                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                                finish()
                                            }
                                        } else {
                                            // Dokumen tidak ditemukan di Firestore
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // Handle kesalahan saat mengambil data dari Firestore
                                        Log.e("LoginActivity", "Error getting user document", exception)
                                    }
                            }
                        } else {
                            // Gagal login, tampilkan pesan kesalahan
                            binding.overlayLoading.visibility = View.GONE
                            Toast.makeText(this, "Login gagal.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                email.isErrorEnabled = false
                password.isErrorEnabled = false

                if (!isEmailValid(inputEmail)) {
                    email.error = getString(R.string.invalid_email_address)
                }

                if (!isPasswordValid(inputPassword)) {
                    password.error = getString(R.string.minimal_8_character)
                }
            }
        }
    }


    private fun validEmail() {
        val inputEmailLayout = binding.tlEmail
        val inputEmail = binding.tlEmail.editText
        inputEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {}

            override fun onTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val email = p0.toString()
                val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (!isValid) {
                    inputEmailLayout.error = getString(R.string.invalid_email_address)
                } else {
                    // Hapus pesan kesalahan jika email valid
                    inputEmailLayout.isErrorEnabled = false
                    inputEmailLayout.error = null
                }
            }
        })

        val inputPassowrdLayout = binding.tlPassword
        val inputPassword = binding.tlPassword.editText
        inputPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {}

            override fun onTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {
                if (!isPasswordValid(inputPassword.toString())) {
                    inputPassowrdLayout.error = getString(R.string.minimal_8_character)
                } else {
                    // Hapus pesan kesalahan jika email valid
                    inputPassowrdLayout.isErrorEnabled = false
                    inputPassowrdLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!isPasswordValid(inputPassword.toString())) {
                    inputPassowrdLayout.error = getString(R.string.minimal_8_character)
                } else {
                    // Hapus pesan kesalahan jika email valid
                    inputPassowrdLayout.isErrorEnabled = false
                    inputPassowrdLayout.error = null
                }
            }
        })
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
}