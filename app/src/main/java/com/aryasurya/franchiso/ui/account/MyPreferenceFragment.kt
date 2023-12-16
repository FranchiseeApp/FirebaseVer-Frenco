package com.aryasurya.franchiso.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.ui.editprofile.EditProfileActivity
import com.aryasurya.franchiso.ui.login.LoginActivity
import com.aryasurya.franchiso.utils.DarkMode

class MyPreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var sessionManager: SessionManager
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val editProfile = findPreference<Preference>("edit_account")
        editProfile?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            true
        }

        val theme = findPreference<ListPreference>(getString(R.string.pref_key_dark))
        theme?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                "auto" -> updateTheme(DarkMode.FOLLOW_SYSTEM.value)
                "on" -> updateTheme(DarkMode.ON.value)
                "off" -> updateTheme(DarkMode.OFF.value)
            }
            true
        }

        sessionManager = SessionManager(requireContext())

        val logoutPreference = findPreference<Preference>("logout")
        logoutPreference?.setOnPreferenceClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(getString(R.string.are_you_sure_you_want_to_log_out))
            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                sessionManager.clearSession()
                dialog.dismiss()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finishAffinity()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
            true
        }

    }

    private fun updateTheme(mode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(mode)
        requireActivity().recreate()
        return true
    }
}