package com.kantek.coroutines.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.support.core.base.BaseViewModel
import android.support.core.extensions.mapLaunch
import com.kantek.coroutines.models.Album
import com.kantek.coroutines.repository.AlbumRepository

class AlbumViewModel(albumRepository: AlbumRepository) : BaseViewModel() {
    val album = MutableLiveData<Album>()
    val photos = album.mapLaunch(this) {
        albumRepository.getPhotos(it!!.id)
    }
}
