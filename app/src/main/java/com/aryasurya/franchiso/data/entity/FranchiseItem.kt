package com.aryasurya.franchiso.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FranchiseItem(
    var type: String,
    var facility: String,
    var price: String
) : Parcelable

data class FranchiseData(
    val userId: String?,
    val name: String,
    val address: String,
    val description: String,
    val category: String,
    val phoneNumber: String,
    val franchiseTypes: List<FranchiseItem>,
    val images: List<String> // atau model lain untuk gambar
)
