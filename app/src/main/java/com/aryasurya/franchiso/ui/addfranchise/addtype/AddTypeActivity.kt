package com.aryasurya.franchiso.ui.addfranchise.addtype

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.FranchiseItem
import com.aryasurya.franchiso.databinding.ActivityAddTypeBinding

class AddTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTypeBinding
    private var editedItem: FranchiseItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            "Stand",
            "Store",
            "Kios",
            "Outlet",
            "Resto",
            "Mini market")
        val adapter = ArrayAdapter(this@AddTypeActivity, R.layout.text_type_franchise, items)
        binding.autoCompleteTextView.setAdapter(adapter)


//        binding.btnSaveType.setOnClickListener {
//            saveType()
//        }
        editedItem = intent.getParcelableExtra(EXTRA_TYPE_ITEM)

        // Jika ada item yang akan diedit, tampilkan datanya di input fields
        editedItem?.let { item ->
            binding.autoCompleteTextView.setText(item.type)
            binding.tlFacility.editText?.setText(item.facility)
            binding.tlPrice.editText?.setText(item.price)
        }

        binding.btnSaveType.setOnClickListener {
            saveEditedItem() // Panggil fungsi saveEditedItem() saat tombol "Save" ditekan
        }
    }

    private fun saveEditedItem() {
        val editedType = createEditedType() // Mendapatkan data yang sudah diedit dari input fields

        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_TYPE_ITEM, editedType)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun createEditedType(): FranchiseItem {
        val editedName = binding.autoCompleteTextView.text.toString()
        val editedFacility = binding.tlFacility.editText?.text.toString()
        val editedPrice = binding.tlPrice.editText?.text.toString()

        // membuat objek FranchiseItem baru dengan data yang sudah diedit
        return FranchiseItem(editedName, editedFacility, editedPrice)
    }

    companion object {
        const val EXTRA_TYPE_ITEM = "extra_type_item"
    }
}