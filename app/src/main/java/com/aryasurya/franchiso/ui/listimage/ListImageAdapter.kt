package com.aryasurya.franchiso.ui.listimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aryasurya.franchiso.databinding.ItemImageBinding
import com.bumptech.glide.Glide

class ListImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ListImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            // Bind URL gambar ke ImageView menggunakan Glide
            Glide.with(binding.root.context).load(imageUrl).into(binding.imageFranchise)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.bind(imageUrl)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}
