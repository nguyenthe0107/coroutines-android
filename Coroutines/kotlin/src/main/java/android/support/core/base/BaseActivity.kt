package android.support.core.base

import android.content.Intent
import android.support.core.functional.Dispatcher
import android.support.core.lifecycle.ResultLifecycle
import android.support.core.lifecycle.ResultRegistry
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), Dispatcher {

    val resultLife: ResultLifecycle = ResultRegistry()

    override fun onBackPressed() {
        val isFragmentBackPressed = (resultLife as ResultRegistry).backPresses.fold(false) { acc, backable ->
            acc || backable.onBackPressed()
        }
        if (!isFragmentBackPressed) super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (resultLife as ResultRegistry).handleActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        (resultLife as ResultRegistry).handlePermissionsResult(requestCode, permissions, grantResults)
    }
}