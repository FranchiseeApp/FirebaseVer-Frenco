package com.aryasurya.franchiso.ui.editprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.User
import com.aryasurya.franchiso.databinding.ActivityEditProfileBinding
import com.aryasurya.franchiso.ui.login.UserViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val name = binding.tiNameRegister.editText?.text.toString()
            val noTel = binding.tiNumberTel.editText?.text.toString()
            val gender = binding.chooseGender.editText?.text.toString()

            updateUserSpecificData(name, noTel, gender)
        }

        fillUserProfileData()

    }

    private fun fillUserProfileData() {
        // Di sini, Anda dapat mengambil data pengguna dari ViewModel
        val user = viewModel.userData.value

        // Gunakan data pengguna untuk mengisi input field pada tampilan
        binding.tiUsernameRegister.editText?.setText(user?.username)
        binding.tiNameRegister.editText?.setText(user?.name)
        binding.tiEmailRegister.editText?.setText(user?.email)
        // Isi input field lainnya sesuai kebutuhan
    }

//    private fun updateUserProfile() {
//        val updatedUser = User(
//            // Ambil nilai dari field input pada tampilan
//            username = binding.tiUsernameRegister.editText?.text.toString(),
//            name = binding.tiNameRegister.editText?.text.toString(),
//            email = binding.tiEmailRegister.editText?.text.toString(),
//            // Isi field lainnya sesuai kebutuhan
//        )
//
//        val firebaseUser = viewModel.getCurrentUser()
//
//        if (firebaseUser != null) {
//            val userId = firebaseUser.uid // Mendapatkan ID pengguna yang terotentikasi
//
//            viewModel.updateUserData(userId, updatedUser) { success ->
//                if (success) {
//                    // Berhasil memperbarui profil pengguna
//                    // Tampilkan pesan atau lakukan tindakan sesuai
//                    finish() // Sebagai contoh, kembali ke layar sebelumnya setelah pembaruan profil berhasil
//                } else {
//                    // Gagal memperbarui profil pengguna
//                    // Tampilkan pesan kesalahan kepada pengguna
//                }
//            }
//        } else {
//            // Pengguna tidak terotentikasi, lakukan sesuatu (contohnya, minta pengguna untuk login kembali)
//        }
//    }

    private fun updateUserSpecificData(name : String, notel: String, gender : String) {

        val userId = "user_id" // Ganti dengan ID pengguna yang terkait
        val updatedFields = mapOf(
            "name" to name,
            "notel" to notel,
            "gender" to gender,
        )

        viewModel.updateSpecificUserData(userId, updatedFields) { success ->
            if (success) {
                // Berhasil memperbarui data pengguna
                // Lakukan tindakan yang sesuai, seperti menampilkan pesan atau update UI

                Toast.makeText(this, "Update data successful", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // Gagal memperbarui data pengguna
                // Tampilkan pesan kesalahan kepada pengguna atau lakukan tindakan lainnya
                Toast.makeText(this, "Update data failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}