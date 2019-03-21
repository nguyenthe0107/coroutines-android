package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import android.support.core.helpers.TemporaryData
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.extensions.call
import com.kantek.coroutines.models.Album
import com.kantek.coroutines.models.Photo
import java.util.concurrent.TimeUnit

class AlbumRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mAlbums = TemporaryData<String, MutableList<Album>>(1, TimeUnit.MINUTES)
    private val mPhotos = TemporaryData<String, MutableList<Photo>>(1, TimeUnit.MINUTES)

    suspend fun getAlbums() = withIO {
        val userId = appCache.user!!.id
        mAlbums.getOrLoad(userId) { apiService.getAlbums(userId).call() }
    }

    suspend fun getPhotos(albumId: String) = withIO {
        mPhotos.getOrLoad(albumId) { apiService.getPhotos(albumId).call() }
    }
}
