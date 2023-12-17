package com.aryasurya.franchiso.ui.addfranchise

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.FranchiseItem

class ImageAdapter(private val imageList: List<Uri>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.shapeableImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_input, parent, false)
        return ImageViewHolder(view)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentImage = imageList[position]
        Glide.with(holder.itemView.context)
            .load(currentImage)
            .centerCrop()
//            .placeholder(R.drawable.placeholder_image) // Placeholder jika gambar sedang dimuat
//            .error(R.drawable.error_image) // Gambar yang ditampilkan jika ada kesalahan
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
