package com.aryasurya.franchiso.ui.listimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryasurya.franchiso.data.entity.FranchiseData
import com.aryasurya.franchiso.databinding.ActivityListImageBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListImageBinding
    private lateinit var adapter: ListImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Tambahkan tombol kembali (tombol panah)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Atur judul toolbar
        supportActionBar?.title = "ListImage"

        val franchiseId = intent.getStringExtra("franchiseId")

        binding.overlayLoading.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("franchises").document(franchiseId!!)
        userDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val franchiseData = documentSnapshot.toObject(FranchiseData::class.java)
                    if (franchiseData != null) {
                        // Ambil data dan terapkan pada adapter
                        adapter = ListImageAdapter(franchiseData.images)
                        binding.rvListImage.adapter = adapter

                        // Set layout manager untuk RecyclerView
                        val imageLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rvListImage.layoutManager = imageLayoutManager
                        binding.overlayLoading.visibility = View.GONE
                    }
                } else {
                    // Dokumen tidak ditemukan di Firestore
                    binding.overlayLoading.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                binding.overlayLoading.visibility = View.GONE
                // Handle kesalahan saat mengambil data dari Firestore
                Log.e("ListImageActivity", "Error getting franchise document", exception)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}