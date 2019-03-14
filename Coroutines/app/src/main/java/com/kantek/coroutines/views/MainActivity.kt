package com.kantek.coroutines.views

import android.support.core.annotations.LayoutId
import android.support.core.extensions.close
import android.support.core.extensions.open
import android.view.Menu
import android.view.MenuItem
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.viewmodel.MainViewModel

@LayoutId(R.layout.activity_main)
class MainActivity : AppActivity<MainViewModel>() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        open(LoginActivity::class).close()
        return super.onOptionsItemSelected(item)
    }
}