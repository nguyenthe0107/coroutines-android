package com.kantek.coroutines.views

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.view.Menu
import android.view.MenuItem
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.extensions.setupStatusChanged
import com.kantek.coroutines.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

@LayoutId(R.layout.activity_main)
class MainActivity : AppActivity<MainViewModel>() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        LoginActivity.show(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appEvent.networkChanged.listen(this) {
            txtNetworkStatus.setupStatusChanged(it!!)
        }
    }
}
