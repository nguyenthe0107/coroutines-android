package android.support.core.base

import android.content.Intent
import android.os.Bundle
import android.support.core.extensions.dispatchHidden
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.Backable
import android.support.core.lifecycle.ResultLifecycle
import android.support.core.lifecycle.ResultRegistry
import android.support.core.lifecycle.ViewLifecycleOwner
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View

/**
 * Custom for lifecycle
 */
abstract class BaseFragment : Fragment(), Backable {
    private val TAG: String = this.javaClass.simpleName

    val resultLife: ResultLifecycle = ResultRegistry()
    val viewLife = ViewLifecycleOwner()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLife.lifecycle.create()
        Log.i(TAG, "Created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLife.lifecycle.destroy()
        Log.i(TAG, "Destroy")
    }

    override fun onStart() {
        super.onStart()
        if (isVisibleOnScreen()) {
            onFragmentStarted()
            viewLife.lifecycle.start()
            Log.i(TAG, "Start")
        }
    }

    override fun onStop() {
        super.onStop()
        if (isVisibleOnScreen()) {
            onFragmentStopped()
            viewLife.lifecycle.stop()
            Log.i(TAG, "Stop")
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVisibleOnScreen()) {
            viewLife.lifecycle.resume()
            Log.i(TAG, "Resume")
        }
    }

    override fun onPause() {
        super.onPause()
        if (isVisibleOnScreen()) {
            viewLife.lifecycle.pause()
            Log.i(TAG, "Pause")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            onFragmentStopped()
            viewLife.lifecycle.pause().stop()
            Log.i(TAG, "Hide")
        } else {
            onFragmentStarted()
            viewLife.lifecycle.start().resume()
            Log.i(TAG, "Show")
        }
        dispatchHidden(hidden)
    }

    protected open fun onFragmentStarted() {
        (activity as? android.support.core.base.BaseActivity)?.also {
            (it.resultLife as ResultRegistry).backPresses.add(this)
        }
    }

    protected open fun onFragmentStopped() {
        (activity as? android.support.core.base.BaseActivity)?.also {
            (it.resultLife as ResultRegistry).backPresses.remove(this)
        }
    }

    override fun onBackPressed() = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (resultLife as ResultRegistry).handleActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        (resultLife as ResultRegistry).handlePermissionsResult(requestCode, permissions, grantResults)
    }
}