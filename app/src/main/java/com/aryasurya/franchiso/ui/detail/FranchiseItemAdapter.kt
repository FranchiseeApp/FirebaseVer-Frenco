package com.aryasurya.franchiso.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aryasurya.franchiso.data.entity.FranchiseItem
import com.aryasurya.franchiso.databinding.ItemTypeDetailBinding

class FranchiseItemAdapter(
    private val items: List<FranchiseItem>,
    private val onItemClick: (FranchiseItem) -> Unit
) : RecyclerView.Adapter<FranchiseItemAdapter.FranchiseItemViewHolder>() {

    inner class FranchiseItemViewHolder(val binding: ItemTypeDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(franchiseItem: FranchiseItem) {
            // Bind data ke elemen UI dalam item menggunakan ViewBinding
            binding.tvNameFranchises.text = franchiseItem.type

            itemView.setOnClickListener {
                onItemClick(franchiseItem)
            }
            // Set image based on type
//            if (franchiseItem.type == "Store") {
//                // Set image for Store type
//                binding.ivFranchiseIcon.setImageResource(R.drawable.baseline_storefront_24)
//            } else {
//                // Set default image or another image for other types
//                binding.ivFranchiseIcon.setImageResource(R.drawable.baseline_store_24)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FranchiseItemViewHolder {
        val binding = ItemTypeDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FranchiseItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FranchiseItemViewHolder, position: Int) {
        val franchiseItem = items[position]

        holder.bind(franchiseItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
