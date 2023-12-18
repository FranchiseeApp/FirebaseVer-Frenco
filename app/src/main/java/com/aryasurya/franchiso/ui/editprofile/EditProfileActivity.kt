package com.aryasurya.franchiso.ui.editprofile

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.aryasurya.franchiso.ui.camera.CameraActivity
import com.aryasurya.franchiso.ui.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.User
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.databinding.ActivityEditProfileBinding
import com.aryasurya.franchiso.ui.account.AccountFragment
import com.aryasurya.franchiso.ui.login.UserViewModel
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    // CEK PERMISSION IMAGE
    private var currentImageUri: Uri? = null

    private val requestPermissLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.permission_request_granted), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.permission_request_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissLauncher.launch(REQUIRED_PERMISSION)
        }

        sessionManager = SessionManager(this)
        val user: User? = sessionManager.getSession()

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.btnChangePhotoProfile.setOnClickListener {
            selectImage()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.tiNameRegister.editText?.text.toString()
            val noTel = binding.tiNumberTel.editText?.text.toString()
            val gender = binding.chooseGender.editText?.text.toString()

            if (user != null) {
                currentImageUri?.let { it1 ->
                    updateUserSpecificData(user.userId, name, noTel, gender,
                        it1
                    )
                }
            }
        }

        fillUserProfileData()

    }

    private fun fillUserProfileData() {
        val user: User? = sessionManager.getSession()

        // Gunakan data pengguna untuk mengisi input field pada tampilan
        binding.tiUsernameRegister.editText?.setText(user?.username)
        binding.tiNameRegister.editText?.setText(user?.name)
        binding.tiEmailRegister.editText?.setText(user?.email)
        binding.tiNumberTel.editText?.setText(user?.noTel)
        Glide.with(this).load(user?.photoProfileUrl).into(binding.ivProfile)
        val genderOptions = arrayOf("Male", "Female")

        // Atur adapter untuk AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.autoCompleteTextView.setAdapter(adapter)

        // Periksa apakah data gender pengguna ada di dalam daftar opsi
        user?.gender?.let { userGender ->
            val selectedPosition = genderOptions.indexOf(userGender)
            if (selectedPosition != -1) {
                // Pilih opsi gender yang sesuai dengan data pengguna
                binding.autoCompleteTextView.setText(genderOptions[selectedPosition], false)
            }
        }
    }

    private fun updateUserSpecificData(userId: String, name : String, noTel: String, gender : String, uri: Uri) {

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imagesRef = storageRef.child("profile_photos")

        val timestamp = System.currentTimeMillis() // Dapatkan timestamp saat ini
        val fileName = "profile_$timestamp.jpg"

         // Ubah ini sesuai dengan URI foto yang ingin Anda unggah
        val imageRef = imagesRef.child(fileName) // Ganti "profile.jpg" dengan nama file yang ingin Anda gunakan

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            // Foto berhasil diunggah
            // Dapatkan URL unduhan gambar dari Firebase Storage
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Dapatkan URL gambar dari 'uri'
                val imageUrl = uri.toString()

                val updatedFields = mapOf(
                    "name" to name,
                    "noTel" to noTel,
                    "gender" to gender,
                    "photoProfileUrl" to imageUrl
                )
                // Gunakan imageUrl untuk menyimpan URL gambar ini ke database atau tempat penyimpanan data pengguna lainnya
                viewModel.updateSpecificUserData(userId, updatedFields) { success ->
                    if (success) {
                        // Berhasil memperbarui data pengguna
                        sessionManager.updateSessionName(name)
                        sessionManager.updateSessionNoTel(noTel)
                        sessionManager.updateSessionGender(gender)
                        sessionManager.updateSessionPhotoProfile(imageUrl)

                        val accountFragment = supportFragmentManager.findFragmentByTag("AccountFragment") as AccountFragment?
                        accountFragment?.updateSessionData(name, noTel, gender, imageUrl)
                        finish()
                    } else {
                        // Gagal memperbarui data pengguna
                        // Tampilkan pesan kesalahan kepada pengguna atau lakukan tindakan lainnya
                        Toast.makeText(this, "Update data failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                // Gagal mendapatkan URL gambar
                Log.e("Firebase Storage", "Error getting download URL: $exception")
            }
        }.addOnFailureListener { exception ->
            // Gagal mengunggah foto
            Log.e("Firebase Storage", "Error uploading image: $exception")
        }

    }


    private fun selectImage() {
        val optionActions = arrayOf<CharSequence>(getString(R.string.take_photo),
            getString(R.string.gallery), getString(R.string.cancel))
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(getString(R.string.choose_from))
        dialogBuilder.setIcon(R.mipmap.ic_launcher)
        dialogBuilder.setItems(optionActions) { dialogInterface, i ->
            when(i) {
                0 -> {
                    startCameraX()
                }
                1 -> {
                    startGallery()
                }
                2 -> {
                    dialogInterface.dismiss()
                }
            }
        }
        dialogBuilder.show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI" , "showImage: $it")
            binding.ivProfile.setImageURI(it)
        }
    }

    companion object {
        private  const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }


}