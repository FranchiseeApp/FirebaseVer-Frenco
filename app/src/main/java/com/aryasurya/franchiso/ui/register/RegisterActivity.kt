package com.aryasurya.franchiso.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.databinding.ActivityRegisterBinding
import com.aryasurya.franchiso.ui.login.LoginActivity
import com.aryasurya.franchiso.ui.login.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel : UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.tvLoginHere.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        validEmail()
        matchPassword()
        activeButton()
    }
    private fun activeButton() {
        val username = binding.tiUsernameRegister
        val name = binding.tiNameRegister
        val email = binding.tiEmailRegister
        val password = binding.tiPasswordRegister
        val btnRegister = binding.btnRegister

        btnRegister.setOnClickListener {
            val inputUsername = username.editText?.text.toString()
            val inputName = name.editText?.text.toString()
            val inputEmail = email.editText?.text.toString()
            val inputPassword = password.editText?.text.toString()
            val confirmPassword = binding.tiConfirmPasswordRegister.editText?.text.toString()

            if (isUsernameValid(inputUsername) && isNameValid(inputName) && isEmailValid(inputEmail)
                && isPasswordValid(inputPassword) && confirmPassword.isNotEmpty()) {

                binding.overlayLoading.visibility = View.VISIBLE
                viewModel.registerUser(inputEmail, inputPassword, inputUsername, inputName)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registrasi berhasil, lakukan tindakan setelah registrasi
                            binding.overlayLoading.visibility = View.GONE

                            // Lanjutkan ke halaman lain atau tampilkan pesan sukses
                            Toast.makeText(this,  "Register Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()

                        } else {
                            // Registrasi gagal, tampilkan pesan kesalahan
                            binding.overlayLoading.visibility = View.GONE
                            Toast.makeText(this, "Registrasi gagal, Email sudah digunakan.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else {
                username.isErrorEnabled = false
                name.isErrorEnabled = false
                email.isErrorEnabled = false
                password.isErrorEnabled = false

                if (!isUsernameValid(inputUsername)) {
                    username.error = getString(R.string.fill_the_username)
                }

                if (!isNameValid(inputName)) {
                    name.error = "Fill the name"
                }

                if (!isEmailValid(inputEmail)) {
                    email.error = getString(R.string.invalid_email_address)
                }

                if (!isPasswordValid(inputPassword)) {
                    password.error = getString(R.string.minimal_8_character)
                }

            }
        }
    }

    private fun matchPassword() {
        val passwordLayout = binding.tiPasswordRegister
        val password = passwordLayout.editText
        val confirmPasswordLayout = binding.tiConfirmPasswordRegister
        val confirmPassword = confirmPasswordLayout.editText

        password?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {
                if (p0 != null && p0.length <= 7) {
                    passwordLayout.apply {
                        isErrorEnabled = true
                        errorIconDrawable = null
                        error = getString(R.string.eror_text)
                    }
                } else {
                    passwordLayout.apply {
                        isErrorEnabled = false
                        error = null
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {
                    confirmPasswordLayout.isErrorEnabled = true
                    confirmPasswordLayout.error = getString(R.string.input_confirm_password)
                } else {
                    confirmPasswordLayout.isErrorEnabled = false
                    confirmPasswordLayout.error = null
                }
            }

        })

        confirmPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {
                if (p0.toString() != password?.text.toString()) {
                    confirmPasswordLayout.isErrorEnabled = true
                    confirmPasswordLayout.error = getString(R.string.confirm_password_eror_text)
                } else {
                    confirmPasswordLayout.isErrorEnabled = false
                    confirmPasswordLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun validEmail() {
        val inputEmailLayout = binding.tiEmailRegister
        val inputEmail = binding.inputEmailRegister
        inputEmail.addTextChangedListener(object : TextWatcher {
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
    }
    private fun isUsernameValid(username: String): Boolean {
        return username.isNotEmpty()
    }
    private fun isNameValid(name: String): Boolean {
        return name.isNotEmpty()
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }


}