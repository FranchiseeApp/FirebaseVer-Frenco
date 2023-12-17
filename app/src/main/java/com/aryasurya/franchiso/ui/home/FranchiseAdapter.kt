package com.aryasurya.franchiso.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aryasurya.franchiso.data.entity.FranchiseData
import com.aryasurya.franchiso.databinding.StoryItemBinding
import com.bumptech.glide.Glide

class FranchiseAdapter(private val franchiseList: List<FranchiseData>) :
    RecyclerView.Adapter<FranchiseAdapter.FranchiseViewHolder>() {

    inner class FranchiseViewHolder(val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(franchiseData: FranchiseData) {
            // Bind data ke elemen UI dalam item franschise_card menggunakan ViewBinding
            binding.tvNameFranchises.text = franchiseData.name
            binding.tvCategoryFranchise.text = franchiseData.category
            binding.tvDescStory.text = franchiseData.description

            // Tambahkan logika untuk menampilkan gambar jika ada
            // franchiseData.images berisi URI gambar yang diunggah ke Firebase Storage
            // Misalnya:
             Glide.with(binding.root.context).load(franchiseData.images.firstOrNull()).into(binding.ivFranchise)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FranchiseViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FranchiseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FranchiseViewHolder, position: Int) {
        val franchiseData = franchiseList[position]
//        val firstImageUri = franchiseData.images.firstOrNull()
        holder.bind(franchiseData)

//        if (!firstImageUri.isNullOrEmpty()) {
//            Glide.with(holder.itemView)
//                .load(firstImageUri)
//                .into(holder.binding.ivFranchise)
//        } else {
//            // Handle case when there's no image available
//            // You can set a placeholder or hide the ImageView
//        }
    }

    override fun getItemCount(): Int {
        return franchiseList.size
    }
}
