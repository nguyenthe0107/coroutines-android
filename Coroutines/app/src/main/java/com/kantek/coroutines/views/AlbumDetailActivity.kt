package com.kantek.coroutines.views

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.base.BaseFragment
import android.support.core.extensions.argument
import android.support.core.extensions.asArgument
import android.support.core.extensions.observe
import android.support.core.extensions.open
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.models.Album
import com.kantek.coroutines.viewmodel.AlbumViewModel
import com.kantek.coroutines.views.adapters.PhotoAdapter
import kotlinx.android.synthetic.main.activity_album_detail.*

@LayoutId(R.layout.activity_album_detail)
class AlbumDetailActivity : AppActivity<AlbumViewModel>() {
    companion object {
        fun show(from: BaseFragment, it: Album) {
            from.open(AlbumDetailActivity::class, it.asArgument())
        }
    }

    private val mAlbum: Album by argument()

    private lateinit var mPhotoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPhotoAdapter = PhotoAdapter(recvContent)
        viewModel.album.value = mAlbum
        viewModel.photos.observe(this) {
            mPhotoAdapter.items = it
        }
        viewModel.album.observe(this) {
            txtTitle.text = it!!.title
        }
        mPhotoAdapter.onItemClickListener = {
            PhotoActivity.show(this, it)
        }
    }

    override fun onDestroy() {
        mPhotoAdapter.items = null
        super.onDestroy()
    }
}
