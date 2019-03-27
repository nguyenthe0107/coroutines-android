package com.kantek.coroutines.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.support.core.base.BaseViewModel
import android.support.core.extensions.map
import com.kantek.coroutines.datasource.AppEvent
import com.kantek.coroutines.models.Album
import com.kantek.coroutines.repository.AlbumRepository

class AlbumViewModel(
    albumRepository: AlbumRepository
) : BaseViewModel() {
    val album = MutableLiveData<Album>()

    val photos = album.map(this) {
        albumRepository.getPhotos(it!!.id)
    }
}
