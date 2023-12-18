package com.aryasurya.franchiso.ui.detail

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryasurya.franchiso.data.entity.FranchiseData
import com.aryasurya.franchiso.data.entity.FranchiseItem
import com.aryasurya.franchiso.databinding.ActivityDetailBinding
import com.aryasurya.franchiso.databinding.BottomSheetBinding
import com.aryasurya.franchiso.databinding.BottomSheetOptionsBinding
import com.aryasurya.franchiso.ui.bottomsheet.ModalBottomSheet
import com.aryasurya.franchiso.ui.bottomsheet.ModalBottomSheetOptions
import com.aryasurya.franchiso.ui.listimage.ListImageActivity
import com.aryasurya.franchiso.utils.formatNumber
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var adapter: FranchiseItemAdapter

    private var franchiseId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        franchiseId = intent.getStringExtra("franchiseId")

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Tampilkan tombol kembali
            setDisplayShowHomeEnabled(true)
            // Tambahkan aksi saat tombol kembali diklik
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }

//        Log.d("DetailActivity", "Received franchiseId: $franchiseId")
        binding.overlayLoading.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("franchises").document(franchiseId!!)
        userDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val franchiseData = documentSnapshot.toObject(FranchiseData::class.java)
                    if (franchiseData != null) {
                        Log.d("detailData", "$franchiseData")


                        val imagesFranchise = franchiseData.images

                        Glide.with(this).load(imagesFranchise[0]).into(binding.bigImage)
                        Glide.with(this).load(imagesFranchise[1]).into(binding.smallImage1)
                        Glide.with(this).load(imagesFranchise[2]).into(binding.smallImage2)
                        Glide.with(this).load(imagesFranchise[3]).into(binding.smallImage3)

                        binding.btnLoadMore.setOnClickListener {
                            val intent = Intent(this@DetailActivity, ListImageActivity::class.java)
                            intent.putExtra("franchiseId", franchiseData.documentId)

                            startActivity(intent)
                        }


                        Log.d("imagesF", "onCreate: ${imagesFranchise.size}")

                        if (imagesFranchise.size <= 4) {
                            binding.btnLoadMore.visibility = View.GONE
                        } else {
                            binding.btnLoadMore.visibility = View.VISIBLE
                            val updateText = imagesFranchise.size - 4
                            binding.btnLoadMore.text = "+" + updateText
                        }

                        // == NAME FRANCHISE
                        binding.tvNameFranchises.text = franchiseData.name


                        // == TYPE FRANCHISE
                        val franchiseTypes = franchiseData.franchiseTypes
//                        Log.d("rvApp", "onCreate: $franchiseTypes")
                        adapter = FranchiseItemAdapter(franchiseTypes) { clickedItem ->

                            // Ketika item diklik, tampilkan modal bottom sheet dan tampilkan informasi yang diperlukan
                            val modalBottomSheet = ModalBottomSheet(clickedItem)
                            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)

                        }
                        binding.rvTypeFranchise.adapter = adapter

                        val imageLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        binding.rvTypeFranchise.layoutManager = imageLayoutManager


                        // == PRICE
                        val prices = franchiseTypes.map { it.price }

                        if (prices.size == 1) {
                            binding.tvPriceFranchises.text = "Rp" + formatNumber(prices[0])
                        } else {
                            val minPrice = prices.minOrNull()
                            val maxPrice = prices.maxOrNull()

                            if (minPrice != null && maxPrice != null) {
//                                binding.tvPriceFranchises.text = "$minPrice - $maxPrice"
                                binding.tvPriceFranchises.text = "Rp" + formatNumber(minPrice) + " - " + "Rp" + formatNumber(maxPrice)
                            } else {
                                // Handle jika tidak ada harga atau hanya ada satu harga
                                binding.tvPriceFranchises.text = "Price not available"
                            }
                        }


                        // == DESKRIPSI
                        binding.tvDescFranchises.text = franchiseData.description

                        // == BUTTON WA
                        binding.btnWa.setOnClickListener {
                            val phoneNumber = "6287753231841"
                            val url = "https://wa.me/$phoneNumber"

                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        }
                        binding.overlayLoading.visibility = View.GONE
                    }
                } else {
                    binding.overlayLoading.visibility = View.GONE
                    // Dokumen tidak ditemukan di Firestore
                }
            }
            .addOnFailureListener { exception ->
                binding.overlayLoading.visibility = View.GONE
                // Handle kesalahan saat mengambil data dari Firestore
                Log.e("LoginActivity", "Error getting user document", exception)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.aryasurya.franchiso.R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.aryasurya.franchiso.R.id.action_more -> {
                // Aksi yang ingin Anda lakukan saat tombol "More" ditekan
                franchiseId?.let { id ->
                    val modalBottomSheet = ModalBottomSheetOptions(id)
                    modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
                }
                true
            }
            // Handle item lainnya jika ada
            else -> super.onOptionsItemSelected(item)
        }
    }
}

