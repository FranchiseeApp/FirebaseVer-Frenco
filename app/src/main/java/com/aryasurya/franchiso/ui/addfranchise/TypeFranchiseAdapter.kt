package com.aryasurya.franchiso.ui.addfranchise

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.entity.FranchiseItem
import com.aryasurya.franchiso.databinding.ItemTypeFranchiseInputBinding
import com.aryasurya.franchiso.ui.addfranchise.addtype.AddTypeActivity

//class TypeFranchiseAdapter(
//    private val items: MutableList<FranchiseItem>,
//    private val onItemClick: (Int) -> Unit,
//    private val onDeleteClick: (Int) -> Unit
//) : RecyclerView.Adapter<TypeFranchiseAdapter.ViewHolder>() {
//
//    inner class ViewHolder(private val binding: ItemTypeFranchiseInputBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.root.setOnClickListener {
//                onItemClick(adapterPosition)
//            }
//            itemView.setOnClickListener {
//                showPopupMenu(adapterPosition, it)
//            }
//        }
//
//        private fun showPopupMenu(position: Int, view: View) {
//            val popupMenu = PopupMenu(view.context, view)
//            popupMenu.menuInflater.inflate(R.menu.menu_item_options, popupMenu.menu)
//
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.menu_edit -> {
//                        editItem(position)
//                        true
//                    }
//                    R.id.menu_delete -> {
//                        deleteItem(position)
//                        true
//                    }
//                    R.id.menu_cancel -> {
//                        true
//                    }
//                    else -> false
//                }
//            }
//
//            popupMenu.show()
//        }
//
//        private fun editItem(position: Int) {
//            val editedItem = items[position] // Mengambil item yang ingin di-edit dari daftar items
//            // Mulai intent untuk mengirim data ke AddTypeActivity agar dapat di-edit
//            val intent = Intent(itemView.context, AddTypeActivity::class.java)
//            intent.putExtra(AddTypeActivity.EXTRA_TYPE_ITEM, editedItem)
//            (itemView.context as Activity).startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE)
//        }
//
//        private fun deleteItem(position: Int) {
//            items.removeAt(position) // Menghapus item dari daftar items
//            notifyItemRemoved(position) // Memberi tahu adapter tentang perubahan
//            // Lakukan logika penghapusan permanen dari sumber data jika diperlukan
//        }
//
//        fun bind(item: FranchiseItem) {
//            binding.apply {
//                inputTypeFranchise.text = item.type
//                inputFacilityFranchise.text = item.facility
//                inputPriceFranchise.text = item.price
//            }
//        }
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun addItem(item: FranchiseItem) {
//        items.add(item)
//        notifyDataSetChanged() // Inform adapter that dataset changed
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemTypeFranchiseInputBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentItem = items[position]
//        holder.bind(currentItem)
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    companion object {
//        const val EDIT_ITEM_REQUEST_CODE = 101
//    }
//}

class TypeFranchiseAdapter(
    private val items: MutableList<FranchiseItem>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<TypeFranchiseAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTypeFranchiseInputBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FranchiseItem) {
            binding.inputTypeFranchise.text = item.type
            binding.inputFacilityFranchise.text = item.facility
            binding.inputPriceFranchise.text = item.price

            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTypeFranchiseInputBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: FranchiseItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItem(position: Int, item: FranchiseItem) {
        if (position != RecyclerView.NO_POSITION) {
            items[position] = item
            notifyItemChanged(position)
        }
    }

    fun removeItem(position: Int) {
        if (position != RecyclerView.NO_POSITION && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

    fun getItem(position: Int): FranchiseItem {
        return items[position]
    }
    fun getItems(): List<FranchiseItem> {
        return items.toList()
    }
}
