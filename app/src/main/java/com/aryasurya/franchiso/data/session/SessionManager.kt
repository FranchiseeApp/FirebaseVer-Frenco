package com.aryasurya.franchiso.data.session

import android.content.Context
import android.content.SharedPreferences
import com.aryasurya.franchiso.data.entity.User

class SessionManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    fun saveSession(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("userId", user.userId)
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.putString("name", user.name)
        editor.putString("photoProfileUrl", user.photoProfileUrl)
        editor.putString("role", user.role)
        editor.putString("gender", user.gender)
        editor.putString("noTel", user.noTel)
        editor.apply()
    }

    fun getSession(): User? {
        val userId = sharedPreferences.getString("userId", null)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        val name = sharedPreferences.getString("name", null)
        val photoProfileUrl = sharedPreferences.getString("photoProfileUrl", null)
        val role = sharedPreferences.getString("role", null)
        val gender = sharedPreferences.getString("gender", null)
        val noTel = sharedPreferences.getString("noTel", null)

        return if (userId != null && username != null && email != null && name != null && photoProfileUrl != null && role != null && gender != null && noTel != null) {
            User(userId, username, email, name, photoProfileUrl, role, gender, noTel)
        } else {
            null
        }
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    fun updateSessionName(name: String) {
        editor.putString("name", name)
        editor.apply()
    }

    // Fungsi untuk memperbarui nomor telepon di session
    fun updateSessionNoTel(noTel: String) {
        editor.putString("noTel", noTel)
        editor.apply()
    }

    // Fungsi untuk memperbarui gender di session
    fun updateSessionGender(gender: String) {
        editor.putString("gender", gender)
        editor.apply()
    }

    fun updateSessionPhotoProfile(photoProfileUrl: String) {
        editor.putString("photoProfileUrl", photoProfileUrl)
        editor.apply()
    }
}