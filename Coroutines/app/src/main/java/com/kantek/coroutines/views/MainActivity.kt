package com.kantek.coroutines.views

import android.os.Bundle
import android.os.Handler
import android.support.core.annotations.LayoutId
import android.support.core.extensions.close
import android.support.core.extensions.open
import android.view.Menu
import android.view.MenuItem
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.extensions.setupStatusChanged
import com.kantek.coroutines.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

@LayoutId(R.layout.activity_main)
class MainActivity : AppActivity<MainViewModel>() {
    private val mDelay by lazy { Handler() }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        open(LoginActivity::class).close()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appEvent.networkChanged.listen(this) {
            txtNetworkStatus.setupStatusChanged(it!!, mDelay)
        }
    }
}
