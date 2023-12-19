package com.aryasurya.franchiso.ui.bottomsheet

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aryasurya.franchiso.data.entity.FranchiseItem
import com.aryasurya.franchiso.databinding.BottomSheetBinding
import com.aryasurya.franchiso.databinding.BottomSheetOptionsBinding
import com.aryasurya.franchiso.ui.addfranchise.AddFranchiseActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class ModalBottomSheet(private val clickedItem: FranchiseItem) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTypeInp.text = clickedItem.type
        binding.tvFacilityInp.text = clickedItem.facility
        binding.tvPriceInp.text = clickedItem.price

        dialog?.setOnShowListener {
            (requireActivity() as? AppCompatActivity)?.findViewById<View>(R.id.content)?.alpha = 0.6f
        }

        // Set alpha ke 1.0 saat bottom sheet ditutup
        dialog?.setOnDismissListener {
            (requireActivity() as? AppCompatActivity)?.findViewById<View>(R.id.content)?.alpha = 1.0f
        }
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

class ModalBottomSheetOptions(private val franchiseId: String) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOptionsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnEdit.setOnClickListener {
            dialog?.dismiss()
            val intent = Intent(requireContext(), AddFranchiseActivity::class.java)
            intent.putExtra("franchiseId", franchiseId)
            startActivity(intent)

        }

        dialog?.setOnShowListener {
            (requireActivity() as? AppCompatActivity)?.findViewById<View>(R.id.content)?.alpha = 0.6f
        }

        // Set alpha ke 1.0 saat bottom sheet ditutup
        dialog?.setOnDismissListener {
            (requireActivity() as? AppCompatActivity)?.findViewById<View>(R.id.content)?.alpha = 1.0f
        }
    }
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Anda yakin ingin menghapus?")
            .setPositiveButton("Ya") { dialog, _ ->
                // Hapus data dari Firestore
                deleteFranchiseData()
                dialog.dismiss()
                activity?.finish()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()

    }

    private fun deleteFranchiseData() {
        // Lakukan penghapusan data dari Firestore
        // Misalnya, dengan mengambil referensi dokumen dan melakukan delete()
        val db = FirebaseFirestore.getInstance()
        val franchiseDocument = db.collection("franchises").document(franchiseId)
        franchiseDocument.delete()
            .addOnSuccessListener {
                // Handle sukses penghapusan
                Toast.makeText(requireContext(), "Franchise dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle jika terjadi kegagalan saat penghapusan
                Toast.makeText(requireContext(), "Gagal menghapus franchise", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}