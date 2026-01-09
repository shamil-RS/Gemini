package com.example.geminiai.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: Long = 0L,
    val title: String,
    val createdAt: Long = System.currentTimeMillis() / 1000
) : Parcelable
