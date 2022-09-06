package com.sample.quotesapp.data.models

import com.google.gson.annotations.SerializedName

data class Quotes(
    @SerializedName("_id") val id: String,
    val author: String,
    val authorSlug: String,
    val content: String,
    val dateAdded: String,
    val dateModified: String,
    val length: Int,
    val tags: List<String>
)