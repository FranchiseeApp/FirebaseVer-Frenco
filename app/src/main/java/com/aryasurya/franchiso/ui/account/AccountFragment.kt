//    package com.aryasurya.franchiso.ui.account
//
//    import android.content.Intent
//    import android.os.Bundle
//    import androidx.fragment.app.Fragment
//    import android.view.LayoutInflater
//    import android.view.View
//    import android.view.ViewGroup
//    import androidx.appcompat.app.AlertDialog
//    import androidx.appcompat.app.AppCompatDelegate
//    import androidx.fragment.app.viewModels
//    import androidx.preference.ListPreference
//    import androidx.preference.Preference
//    import androidx.preference.PreferenceFragmentCompat
//    import com.aryasurya.franchiso.R
//    import com.aryasurya.franchiso.databinding.FragmentAccountBinding
//    import com.aryasurya.franchiso.ui.login.LoginActivity
//    import com.aryasurya.franchiso.ui.login.LoginViewModel
//    import com.aryasurya.franchiso.utils.DarkMode
//
//    class AccountFragment : Fragment() {
//
//        private lateinit var binding: FragmentAccountBinding
//
//        private val viewModel by viewModels<LoginViewModel> {
//            ViewModelFactory.getInstance(requireContext())
//        }
//
//        override fun onCreateView(
//            inflater: LayoutInflater, container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            // Inflate the layout for this fragment
//            binding = FragmentAccountBinding.inflate(inflater , container , false)
//            return binding.root
//        }
//
//        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//            super.onViewCreated(view, savedInstanceState)
//
//            val preferenceFragment = MyPreferenceFragment()
//            childFragmentManager.beginTransaction()
//                .replace(R.id.settings, preferenceFragment)
//                .commit()
//
//            viewModel.getSession().observe(requireActivity()) {
//                binding.tvNameProfile.text = it.username
//            }
//        }
//
//    }
//
//    class MyPreferenceFragment : PreferenceFragmentCompat() {
//
//        private val viewModel by viewModels<LoginViewModel> {
//            ViewModelFactory.getInstance(requireContext())
//        }
//
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//
//            val theme = findPreference<ListPreference>(getString(R.string.pref_key_dark))
//            theme?.setOnPreferenceChangeListener { _, newValue ->
//                when (newValue) {
//                    "auto" -> updateTheme(DarkMode.FOLLOW_SYSTEM.value)
//                    "on" -> updateTheme(DarkMode.ON.value)
//                    "off" -> updateTheme(DarkMode.OFF.value)
//                }
//                true
//            }
//
//            val logoutPreference = findPreference<Preference>("logout")
//            logoutPreference?.setOnPreferenceClickListener {
//                val builder = AlertDialog.Builder(requireActivity())
//                builder.setMessage(getString(R.string.are_you_sure_you_want_to_log_out))
//                builder.setPositiveButton(getString(R.string.yes)) { dialog , _ ->
//                    viewModel.logout()
//                    dialog.dismiss()
//
//                    val intent = Intent(requireContext(), LoginActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    requireActivity().finishAffinity()
//                }
//                builder.setNegativeButton(getString(R.string.no)) { dialog , _ ->
//                    dialog.dismiss()
//                }
//                val dialog = builder.create()
//                dialog.show()
//                true
//            }
//        }
//
//        private fun updateTheme(mode: Int): Boolean {
//            AppCompatDelegate.setDefaultNightMode(mode)
//            requireActivity().recreate()
//            return true
//        }
//    }