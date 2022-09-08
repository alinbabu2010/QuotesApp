package com.sample.quotesapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Quotes")
data class Quotes(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("_id") val id: String,
    val author: String,
    val authorSlug: String,
    val content: String,
    val dateAdded: String,
    val dateModified: String,
    val length: Int
)