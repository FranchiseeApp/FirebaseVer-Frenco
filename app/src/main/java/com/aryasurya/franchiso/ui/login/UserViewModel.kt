package com.aryasurya.franchiso.ui.login

import androidx.lifecycle.ViewModel
import com.aryasurya.franchiso.data.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()

    fun registerUser(email: String, password: String, username: String, name: String): Task<AuthResult> {
        return userRepository.registerUser(email, password, username, name)
    }

    fun loginUser(email: String, password: String): Task<AuthResult> {
        return userRepository.loginUser(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return userRepository.getCurrentUser()
    }

    fun logoutUser() {
        userRepository.logoutUser()
    }
}