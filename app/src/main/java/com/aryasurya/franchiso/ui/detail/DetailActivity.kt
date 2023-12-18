package com.aryasurya.franchiso.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aryasurya.franchiso.MainActivity
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.FranchiseData
import com.aryasurya.franchiso.data.entity.User
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.databinding.ActivityDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val franchiseId = intent.getStringExtra("franchiseId")

//        Log.d("DetailActivity", "Received franchiseId: $franchiseId")
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("franchises").document(franchiseId!!)
        userDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val franchiseData = documentSnapshot.toObject(FranchiseData::class.java)
                    if (franchiseData != null) {
                        binding.tvNameFranchises.text = franchiseData.name
                    }
                } else {
                    // Dokumen tidak ditemukan di Firestore
                }
            }
            .addOnFailureListener { exception ->
                // Handle kesalahan saat mengambil data dari Firestore
                Log.e("LoginActivity", "Error getting user document", exception)
            }
    }
}