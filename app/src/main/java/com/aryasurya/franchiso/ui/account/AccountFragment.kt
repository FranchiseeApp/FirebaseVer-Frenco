package com.aryasurya.franchiso.ui.account

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.User
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.databinding.FragmentAccountBinding
import com.aryasurya.franchiso.ui.login.LoginActivity
import com.aryasurya.franchiso.ui.login.UserViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: UserViewModel by viewModels()
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

    fun updateSessionData(name: String, noTel: String, gender: String, imageUrl: String) {
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

