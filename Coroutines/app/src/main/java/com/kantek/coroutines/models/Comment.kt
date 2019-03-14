package com.kantek.coroutines.models

import com.google.gson.annotations.SerializedName

class Comment(
    val name: String,
    val email: String,
    @SerializedName("body")
    val comment: String
)