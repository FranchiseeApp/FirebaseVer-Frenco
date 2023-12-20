package com.aryasurya.franchiso.ui.account

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.User
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.databinding.FragmentAccountBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog
    private lateinit var photoProfile: ImageView



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

        progressDialog = ProgressDialog(requireContext())
        photoProfile = binding.ivProfile

        sessionManager = SessionManager(requireContext())
        val user: User? = sessionManager.getSession()

        val preferenceFragment = MyPreferenceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.settings, preferenceFragment)
            .commit()

        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        Log.d(TAG, "onViewCreated: $user")
        
        binding.tvNameProfile.text = user?.name
//        if (user?.photoProfileUrl.isNullOrEmpty()) {
//            binding.ivProfile.setImageResource(R.drawable.image_user_default)
//        } else {
//            Glide.with(requireContext()).load(user?.photoProfileUrl).into(binding.ivProfile)
//        }


    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val refreshedUser: User? = sessionManager.getSession()
        updateSessionData(refreshedUser?.name ?: "", refreshedUser?.noTel ?: "", refreshedUser?.gender ?: "", refreshedUser?.photoProfileUrl ?: "")
    }

    private fun updateSessionData(name: String, noTel: String, gender: String, imageUrl: String) {
        sessionManager.updateSessionName(name)
        sessionManager.updateSessionNoTel(noTel)
        sessionManager.updateSessionGender(gender)
        sessionManager.updateSessionPhotoProfile(imageUrl)

        // Update tampilan dengan data baru
        binding.tvNameProfile.text = name
        if (imageUrl.isEmpty()) {
            binding.ivProfile.setImageResource(R.drawable.image_user_default)
        } else {
            Glide.with(requireContext()).load(imageUrl).into(binding.ivProfile)
        }
    }

    companion object {
        private const val TAG = "AccountFragment"
    }
}

