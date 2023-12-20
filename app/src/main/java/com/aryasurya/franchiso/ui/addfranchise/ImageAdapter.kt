package com.aryasurya.franchiso.ui.addfranchise

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aryasurya.franchiso.R

class ImageAdapter(private val imageList: MutableList<Uri>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.shapeableImageView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
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

        holder.deleteButton.setOnClickListener {
            showDeletePopupMenu(holder.deleteButton, position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    private fun showDeletePopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_delete_cancel, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
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

    private fun deleteItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imageList.size)
    }


}
