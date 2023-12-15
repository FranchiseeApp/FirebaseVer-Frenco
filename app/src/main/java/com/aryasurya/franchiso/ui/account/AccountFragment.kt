    package com.aryasurya.franchiso.ui.account

    import android.content.Intent
    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatDelegate
    import androidx.fragment.app.viewModels
    import androidx.preference.ListPreference
    import androidx.preference.Preference
    import androidx.preference.PreferenceFragmentCompat
    import com.aryasurya.franchiso.R
    import com.aryasurya.franchiso.data.entity.User
    import com.aryasurya.franchiso.data.session.SessionManager
    import com.aryasurya.franchiso.databinding.FragmentAccountBinding
    import com.aryasurya.franchiso.ui.login.LoginActivity
    import com.aryasurya.franchiso.ui.login.UserViewModel
    import com.aryasurya.franchiso.utils.DarkMode

    class AccountFragment : Fragment() {

        private lateinit var binding: FragmentAccountBinding
        private lateinit var sessionManager: SessionManager
        private val viewModel: UserViewModel by viewModels()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Inflate the layout for this fragment
            binding = FragmentAccountBinding.inflate(inflater , container , false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            sessionManager = SessionManager(requireContext())
            val user: User? = sessionManager.getSession()

            val preferenceFragment = MyPreferenceFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.settings, preferenceFragment)
                .commit()

            if (user != null) {
                viewModel.fetchUserData(user.userId)
            } else {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }


            viewModel.userData.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.tvNameProfile.text = user.name
                } else {
                    Toast.makeText(requireContext(), "Check your connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    class MyPreferenceFragment : PreferenceFragmentCompat() {

        private lateinit var sessionManager: SessionManager
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)


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
                builder.setPositiveButton(getString(R.string.yes)) { dialog , _ ->
                    sessionManager.clearSession()
                    dialog.dismiss()

                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finishAffinity()
                }
                builder.setNegativeButton(getString(R.string.no)) { dialog , _ ->
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