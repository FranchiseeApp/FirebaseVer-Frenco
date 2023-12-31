package com.aryasurya.franchiso.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class FranchiseItem(
    var type: String = "",
    var facility: String = "",
    var price: String = ""
) : Parcelable

data class FranchiseData(
    val userId: String? = null,
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val category: String = "",
    val phoneNumber: String = "",
    val franchiseTypes: List<FranchiseItem> = emptyList(),
    val images: List<String> = emptyList(), // atau model lain untuk gambar
    var documentId: String = "",
)