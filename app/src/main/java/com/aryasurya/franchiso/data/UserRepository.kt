package com.aryasurya.franchiso.data

import android.util.Log
import com.aryasurya.franchiso.data.entity.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UserRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun registerUser(email: String, password: String, username: String, name: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
            .onSuccessTask { authResult ->
                val user = authResult.user
                // Jika registrasi berhasil, simpan data pengguna ke Firestore
                val newUser = hashMapOf(
                    "userId" to user?.uid,
                    "username" to username,
                    "email" to email,
                    "name" to name,
                    "role" to "franchisor"
                )

                user?.let {
                    db.collection("users")
                        .document(it.uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            Log.d("RegisterUser", "DocumentSnapshot added with ID: ${user.uid}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("RegisterUser", "Error adding document", e)
                        }
                }

                Tasks.forResult(authResult)
            }
    }

    fun loginUser(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun getUserData(userId: String, onComplete: (User?) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    onComplete(user)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                onComplete(null)
                Log.w("GetDataUser", "Error getting user data", e)
            }
    }
}