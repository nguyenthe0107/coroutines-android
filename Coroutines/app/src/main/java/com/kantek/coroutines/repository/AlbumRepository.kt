package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import com.kantek.coroutines.models.Album
import com.kantek.coroutines.models.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mAlbums = hashMapOf<String, MutableList<Album>>()
    private val mPhotos = hashMapOf<String, MutableList<Photo>>()

    suspend fun getAlbums() = withContext(Dispatchers.IO) {
        val userId = appCache.user!!.id
        if (mAlbums.containsKey(userId)) return@withContext mAlbums[userId]
        apiService.getAlbums(userId).call().also {
            mAlbums[userId] = it
        }
    }

    suspend fun getPhotos(albumId:String) = withContext(Dispatchers.IO) {
        if (mPhotos.containsKey(albumId)) return@withContext mPhotos[albumId]
        apiService.getPhotos(albumId).call().also {
            mPhotos[albumId] = it
        }
    }
}
