package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ItemListRequest(
    val search: String?,
    val price: Int?,
    val keywords: List<String>?
)