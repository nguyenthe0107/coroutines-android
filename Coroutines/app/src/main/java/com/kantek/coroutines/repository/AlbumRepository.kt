package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import com.kantek.coroutines.models.Album
import com.kantek.coroutines.models.Photo

class AlbumRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mAlbums = hashMapOf<String, MutableList<Album>>()
    private val mPhotos = hashMapOf<String, MutableList<Photo>>()

    suspend fun getAlbums() = withIO {
        val userId = appCache.user!!.id
        if (mAlbums.containsKey(userId)) return@withIO mAlbums[userId]
        apiService.getAlbums(userId).call {
            mAlbums[userId] = this
        }
    }

    suspend fun getPhotos(albumId: String) = withIO {
        if (mPhotos.containsKey(albumId)) return@withIO mPhotos[albumId]
        apiService.getPhotos(albumId).call {
            mPhotos[albumId] = this
        }
    }
}
