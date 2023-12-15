package com.aryasurya.franchiso.data.session

import android.content.Context
import android.content.SharedPreferences
import com.aryasurya.franchiso.data.pref.User

class SessionManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveSession(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("userId", user.userId)
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.putString("name", user.name)
        editor.putString("password", user.password)
        editor.putString("role", user.role)
        editor.apply()
    }

    fun getSession(): User? {
        val userId = sharedPreferences.getString("userId", null)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        val name = sharedPreferences.getString("name", null)
        val password = sharedPreferences.getString("password", null)
        val role = sharedPreferences.getString("role", null)

        return if (userId != null && username != null && email != null && name != null && password != null && role != null) {
            User(userId, username, email, name, password, role)
        } else {
            null
        }
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}