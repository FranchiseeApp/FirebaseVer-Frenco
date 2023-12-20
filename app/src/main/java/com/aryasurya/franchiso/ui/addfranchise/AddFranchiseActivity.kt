package com.aryasurya.franchiso.ui.addfranchise

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.FranchiseData
import com.aryasurya.franchiso.data.entity.FranchiseItem
import com.aryasurya.franchiso.databinding.ActivityAddFranchiseBinding
import com.aryasurya.franchiso.ui.addfranchise.addtype.AddTypeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddFranchiseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFranchiseBinding
    private lateinit var adapter: TypeFranchiseAdapter
    private val ADD_TYPE_REQUEST_CODE = 100
    private val EDIT_ITEM_REQUEST_CODE = 101
    private var editedItemPosition: Int = -1 // Untuk menyimpan posisi item yang diedit

    private val REQUEST_IMAGE_PICK = 2
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter

    private var franchiseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFranchiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        franchiseId = intent.getStringExtra("franchiseId")

        franchiseId?.let { id ->
            // Jika franchiseId tidak null, akses data dari Firebase
            fetchDataFromFirebase(id)

        }

        Log.d("AddFranchiseActivity", "fId:  $franchiseId")

        val items = listOf("Food", "Drink")
        val adapterCategory = ArrayAdapter(this@AddFranchiseActivity, R.layout.text_type_franchise, items)
        binding.autoCompleteTextView.setAdapter(adapterCategory)

        // Inisialisasi RecyclerView dan adapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvType.layoutManager = layoutManager

        adapter = TypeFranchiseAdapter(mutableListOf(),
            onItemClick = { position ->
                showPopupMenu(position, binding.rvType.getChildAt(position))
            },
            onDeleteClick = { position ->
                deleteItem(position)
            }
        )
        binding.rvType.adapter = adapter // Atur adapter ke RecyclerView

        binding.btnAddType.setOnClickListener {
            val intent = Intent(this, AddTypeActivity::class.java)
            startActivityForResult(intent, ADD_TYPE_REQUEST_CODE)
        }

        imageAdapter = ImageAdapter(selectedImages)
        binding.rvImageFranchise.adapter = imageAdapter

        val imageLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImageFranchise.layoutManager = imageLayoutManager

        binding.btnAddImg.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnUploadDataFranchise.setOnClickListener {
            binding.overlayLoading.visibility = View.VISIBLE
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            // Panggil fungsi untuk mengumpulkan data dari inputan
            val franchiseName = binding.tlName.editText?.text.toString()
            val franchiseAddress = binding.tlAddress.editText?.text.toString()
            val franchiseDescription = binding.tlDesc.editText?.text.toString()
            val franchiseCategory = binding.autoCompleteTextView.text.toString()
            val franchisePhoneNumber = binding.tlWa.editText?.text.toString()

            // Dapatkan daftar tipe franchisenya dari adapter
            val franchiseTypes = adapter.getItems()


            if (franchiseTypes.isNotEmpty()) {
                val textColorPrimary = getColorFromAttribute(android.R.attr.textColorPrimary)
                binding.tvSecType.setTextColor(textColorPrimary)
                uploadImagesToFirebaseStorage(selectedImages) { imageUrls ->
                    // Setelah berhasil mengunggah gambar, lanjutkan dengan menyimpan URI ke Firestore
                    val franchiseData = FranchiseData(
                        userId = userId,
                        name = franchiseName,
                        address = franchiseAddress,
                        description = franchiseDescription,
                        category = franchiseCategory,
                        phoneNumber = franchisePhoneNumber,
                        franchiseTypes = franchiseTypes,
                        images = imageUrls,
                    )

                    // Panggil fungsi untuk menyimpan data ke Firestore
                    if (franchiseId == null) {
                        uploadDataToFirebase(franchiseData)
                    } else {
                        val franchiseMap = convertFranchiseDataToMap(franchiseData)
                        updateDataToFirebase(franchiseId!!, franchiseMap)
                    }
                }
            } else {
                binding.overlayLoading.visibility = View.GONE
                binding.tvSecType.setTextColor(Color.RED)
                Toast.makeText(this, "Please add at least one Franchise Type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDataFromFirebase(franchiseId: String) {
        binding.overlayLoading.visibility = View.VISIBLE
        // Lakukan akses ke Firestore untuk mendapatkan data franchise sesuai dengan franchiseId
        val db = FirebaseFirestore.getInstance()
        val franchiseDocument = db.collection("franchises").document(franchiseId)

        franchiseDocument.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {

                    // Dokumen franchise ditemukan, isi formulir dengan data yang ada
                    val franchiseData = document.toObject(FranchiseData::class.java)
                    Log.d("fcData", "fetchDataFromFirebase: $franchiseData ")
                    franchiseData?.let {
                        // Isi formulir dengan data yang didapatkan dari Firestore
                        binding.tlName.editText?.setText(it.name)
                        binding.tlAddress.editText?.setText(it.address)
                        binding.tlDesc.editText?.setText(it.description)
                        val items = listOf("Food", "Drink")
                        val adapterCategory = ArrayAdapter(this@AddFranchiseActivity, R.layout.text_type_franchise, items)
                        val selectedCategoryIndex = adapterCategory.getPosition(it.category)
                        if (selectedCategoryIndex != -1) {
                            // Set AutocompleteTextView dengan teks kategori
                            binding.autoCompleteTextView.setText(it.category, false)
                        }
                        binding.tlWa.editText?.setText(it.phoneNumber)

                        // Isi daftar tipe franchise ke adapter jika ada

                        for (franchiseItem in it.franchiseTypes) {
                            adapter.addItem(franchiseItem)
                        }

//                        val imageUris = it.images.map { imageUrl ->
//                            Uri.parse(imageUrl)
//                        }
//                        for(image in imageUris) {
//                            addImageToList(image)
//                        }

                        binding.overlayLoading.visibility = View.GONE
                    }
                } else {
                    binding.overlayLoading.visibility = View.GONE
                    Log.d("FetchData", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                binding.overlayLoading.visibility = View.GONE
                Log.d("FetchData", "get failed with ", exception)
            }
    }

    private fun updateDataToFirebase(franchiseId: String, updatedFields: Map<String, Any>) {
        binding.overlayLoading.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        val franchisesCollection = db.collection("franchises")

        val franchiseDocument = franchisesCollection.document(franchiseId)

        franchiseDocument.update(updatedFields)
            .addOnSuccessListener {
                Log.d("Update Franchise", "DocumentSnapshot updated with ID: $franchiseId")
                Toast.makeText(this, "Update franchise data successfully!", Toast.LENGTH_SHORT).show()
                binding.overlayLoading.visibility = View.GONE
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("Update Franchise", "Error updating document", e)
                Toast.makeText(this, "Failed to update franchise data!", Toast.LENGTH_SHORT).show()
                binding.overlayLoading.visibility = View.GONE
            }
    }
    private fun convertFranchiseDataToMap(franchiseData: FranchiseData): Map<String, Any> {
        val franchiseMap = mutableMapOf<String, Any>()
        franchiseMap["name"] = franchiseData.name
        franchiseMap["address"] = franchiseData.address
        franchiseMap["description"] = franchiseData.description
        franchiseMap["category"] = franchiseData.category
        franchiseMap["phoneNumber"] = franchiseData.phoneNumber
        franchiseMap["franchiseTypes"] = franchiseData.franchiseTypes
        franchiseMap["images"] = franchiseData.images

        return franchiseMap
    }
    private fun uploadDataToFirebase(franchiseData: FranchiseData) {
        val db = FirebaseFirestore.getInstance()
        val franchisesCollection = db.collection("franchises")

        franchisesCollection.add(franchiseData)
            .addOnSuccessListener { documentReference ->
                val uploadedFranchiseId = documentReference.id // Dapatkan ID dokumen yang baru saja ditambahkan
                franchiseData.documentId = uploadedFranchiseId
                updateDocumentInFirestore(uploadedFranchiseId)

                Log.d("Upload Franchise", "DocumentSnapshot added with ID: $uploadedFranchiseId")
                Toast.makeText(this, "Franchise data uploaded successfully!", Toast.LENGTH_SHORT).show()
                binding.overlayLoading.visibility = View.GONE
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("Upload Franchise", "Error adding document", e)
                Toast.makeText(this, "Failed to upload franchise data!", Toast.LENGTH_SHORT).show()
                binding.overlayLoading.visibility = View.GONE
            }

    }

    private fun updateDocumentInFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val franchisesCollection = db.collection("franchises")

        // Buat objek HashMap yang berisi data yang ingin diperbarui
        val updatedData = mapOf(
            "documentId" to documentId // Field yang akan diperbarui
        )

        // pembaruan data di Firestore
        franchisesCollection.document(documentId) // Dokumen yang akan diperbarui
            .update(updatedData)
    }

    private fun uploadImagesToFirebaseStorage(images: List<Uri>, onComplete: (List<String>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val imageUrls = mutableListOf<String>()
        var imagesUploaded = 0

        val maxImageCount = 4
        if (images.size >= maxImageCount) {
            for (imageUri in images) {
                val imageName = "image_${System.currentTimeMillis()}.jpg" // Nama image
                val fileRef = storageRef.child("images/$imageName")

                val uploadTask = fileRef.putFile(imageUri)

                uploadTask.addOnSuccessListener {
                    // Jika berhasil diunggah, dapatkan URI unduhan
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        imageUrls.add(imageUrl)

                        imagesUploaded++
                        if (imagesUploaded == images.size) {
                            // Semua gambar telah diunggah, kembalikan URI gambar
                            onComplete(imageUrls)
                        }
                    }
                }.addOnFailureListener {
                    binding.overlayLoading.visibility = View.GONE
                    // Handle error jika gagal mengunggah gambar
                }
            }
        } else {
            binding.overlayLoading.visibility = View.GONE
            binding.tvSecImage.setTextColor(Color.RED)
            Toast.makeText(this, "Select at least 4 images", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showImagePickerDialog() {
        val options = arrayOf("Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose image from")

        builder.setItems(options) { dialog, which ->
            when (options[which]) {
                "Gallery" -> pickImageFromGallery()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun pickImageFromGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickPhotoIntent.type = "image/*"
        pickPhotoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Memungkinkan pemilihan multiple image
        startActivityForResult(Intent.createChooser(pickPhotoIntent, "Select Picture"), REQUEST_IMAGE_PICK)
    }

    private fun addImageToList(uri: Uri) {
        selectedImages.add(uri)
        val imageAdapter = ImageAdapter(selectedImages)
        binding.rvImageFranchise.adapter = imageAdapter
        imageAdapter.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_TYPE_REQUEST_CODE -> {
                    data?.getParcelableExtra<FranchiseItem>(AddTypeActivity.EXTRA_TYPE_ITEM)?.let { newTypeItem ->
                        adapter.addItem(newTypeItem)
                    }
                }
                EDIT_ITEM_REQUEST_CODE -> {
                    data?.getParcelableExtra<FranchiseItem>(AddTypeActivity.EXTRA_TYPE_ITEM)?.let { editedItem ->
                        adapter.updateItem(editedItemPosition, editedItem)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    if (data?.clipData != null) {
                        val clipData = data.clipData!!
                        for (i in 0 until clipData.itemCount) {
                            val imageUri = clipData.getItemAt(i).uri
                            addImageToList(imageUri)
                        }
                    } else if (data?.data != null) {
                        val imageUri = data.data
                        imageUri?.let {
                            addImageToList(it)
                        }
                    }
                }
            }
        }
    }

    private fun deleteItem(position: Int) {
        adapter.removeItem(position) // Hapus item dari adapter
    }

    private fun showPopupMenu(position: Int, view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_item_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    editItem(position)
                    true
                }
                R.id.menu_delete -> {
                    deleteItem(position)
                    true
                }
                R.id.menu_cancel -> {
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun editItem(position: Int) {
        editedItemPosition = position // Simpan posisi item yang akan diedit
        val editedItem = adapter.getItem(position) // Ambil item yang ingin di-edit dari adapter
        val intent = Intent(this, AddTypeActivity::class.java)
        intent.putExtra(AddTypeActivity.EXTRA_TYPE_ITEM, editedItem)
        startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE)
    }

    private fun Context.getColorFromAttribute(attr: Int): Int {
        val typedValue = TypedValue()
        val theme = theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
}
