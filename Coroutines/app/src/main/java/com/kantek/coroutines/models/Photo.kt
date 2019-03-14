package com.kantek.coroutines.models

import java.io.Serializable

/**
 * "albumId": 1,
"id": 2,
"title": "reprehenderit est deserunt velit ipsam",
"url": "https://via.placeholder.com/600/771796",
"thumbnailUrl": "https://via.placeholder.com/150/771796"
 */
class Photo(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String
):Serializable
