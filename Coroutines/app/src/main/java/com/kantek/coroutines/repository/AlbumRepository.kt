package com.kantek.coroutines.repository

import android.support.core.di.Repository
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

    fun getAlbums() =
        mAlbums.getOrLoad(appCache.user!!.id) { apiService.getAlbums().call() }

    fun getPhotos(albumId: String) =
        mPhotos.getOrLoad(albumId) { apiService.getPhotos(albumId).call() }
}
