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

        if (user != null) {
            viewModel.fetchUserData(user.userId)

        } else {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }


//        viewModel.userData.observe(viewLifecycleOwner) { user ->
//            Log.d(TAG, "data user : $user")
//            if (user != null) {
//                binding.tvNameProfile.text = user.name
////                context?.let { Glide.with(it).load(user.photoProfileUrl).into(photoProfile) }
//                Glide.with(requireContext())
//                    .load(user.photoProfileUrl)
//                    .into(photoProfile)
//
//                Log.d(TAG, "ini tai : ${user.photoProfileUrl}")
//
//            } else {
//                Toast.makeText(requireContext(), "Check your connection", Toast.LENGTH_SHORT).show()
//            }
//        }

//        binding.btnChangePhotoProfile.setOnClickListener{
//            selectImage()
//        }

    }
//    private fun selectImage() {
//        val optionActions = arrayOf<CharSequence>("Take Photo", "Choose from library", "Cancel")
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//        dialogBuilder.setTitle(R.string.app_name)
//        dialogBuilder.setIcon(R.mipmap.ic_launcher)
//        dialogBuilder.setItems(optionActions) { dialogInterface, i ->
//            when(i) {
//                0 -> {
//                    val take = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(take, 10)
//                }
//                1 -> {
//                    val pick = Intent(Intent.ACTION_PICK)
//                    pick.type = "image/*"
//                    startActivityForResult(Intent.createChooser(pick, "select image"), 20)
//                }
//                2 -> {
//                    dialogInterface.dismiss()
//                }
//            }
//        }
//        dialogBuilder.show()
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                10 -> {
//                    val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap
//                    imageBitmap?.let {
//                        uploadImageToFirebase(it)
//                    }
//                }
//                20 -> {
//                    val selectedImage: Uri? = data?.data
//                    selectedImage?.let {
//                        try {
//                            val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
//                            uploadImageToFirebase(imageBitmap)
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun uploadImageToFirebase(imageBitmap: Bitmap) {
//        progressDialog.setMessage("Uploading Image...")
//        progressDialog.show()
//
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        val storageReference = FirebaseStorage.getInstance().reference.child("profile_photos").child(userId!!)
//
//        val baos = ByteArrayOutputStream()
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val imageData: ByteArray = baos.toByteArray()
//
//        storageReference.putBytes(imageData)
//            .addOnSuccessListener { _ ->
//                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
//                    val photoUrl = downloadUri.toString()
//                    Log.d(TAG, "Photo URL : $photoUrl")
//
//                    // Update URL foto profile di database
//                    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
//                    val image = userRef.child("photoProfileUrl").get()
//                    userRef.child("photoProfileUrl").setValue(photoUrl)
//                        .addOnSuccessListener {
//                            Log.d(TAG, "Photo URL updated successfully")
//                            Log.d("data tai lu", "$image")
//                            viewModel.fetchUserData(userId)
//                            // Berhasil mengunggah dan memperbarui URL foto
//                            photoProfile?.let { it1 ->
//                                Glide.with(requireContext())
//                                    .load(photoUrl)
//                                    .into(it1)
//                            }
//                            progressDialog.dismiss() // Menyembunyikan progressDialog
//                            Toast.makeText(requireContext(), "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { e ->
//                            progressDialog.dismiss() // Menyembunyikan progressDialog
//                            // Gagal memperbarui URL foto
//                            Toast.makeText(requireContext(), "Failed to update photoUrl: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            }
//            .addOnFailureListener { e ->
//                // Gagal mengunggah gambar ke Firebase Storage
//                progressDialog.dismiss()
//                Toast.makeText(requireContext(), "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
    companion object {
        private const val TAG = "AccountFragment"
    }
}

