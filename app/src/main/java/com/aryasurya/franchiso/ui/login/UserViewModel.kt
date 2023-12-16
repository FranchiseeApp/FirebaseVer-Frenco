package com.aryasurya.franchiso.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aryasurya.franchiso.data.UserRepository
import com.aryasurya.franchiso.data.entity.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val db = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> get() = _userData

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

    fun fetchUserData(userId: String) {
        userRepository.getUserData(userId) { user ->
            _userData.postValue(user)
        }
    }

    fun updateUserData(userId: String, updatedUser: User, onComplete: (Boolean) -> Unit) {
        userRepository.updateUserData(userId, updatedUser) { success ->
            onComplete(success)
        }
    }

    fun updateSpecificUserData(userId: String, updatedFields: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        db.collection("users")
            .document(userId)
            .update(updatedFields) // Memperbarui bidang-bidang tertentu dalam dokumen
            .addOnSuccessListener {
                onComplete(true) // Berhasil memperbarui data pengguna
            }
            .addOnFailureListener { e ->
                onComplete(false) // Gagal memperbarui data pengguna
                // Handle error appropriately
            }
    }
}