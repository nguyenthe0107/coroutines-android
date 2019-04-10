package android.support.core.base

import android.content.Intent
import android.os.Bundle
import android.support.core.extensions.dispatchHidden
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.Backable
import android.support.core.functional.Dispatcher
import android.support.core.lifecycle.ResultLifecycle
import android.support.core.lifecycle.ResultRegistry
import android.support.core.lifecycle.ViewLifecycleOwner
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View

/**
 * Custom for lifecycle
 */
abstract class BaseFragment : Fragment(), Backable, Dispatcher {
    private val TAG: String = this.javaClass.simpleName

    val resultLife: ResultLifecycle = ResultRegistry()
    val viewLife = ViewLifecycleOwner()
    private val mLifeRegistry get() = viewLife.lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLifeRegistry.create()
        Log.i(TAG, "Created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLifeRegistry.destroy()
        Log.i(TAG, "Destroy")
    }

    override fun onStart() {
        super.onStart()
        if (isVisibleOnScreen()) {
            performStartFragment()
            mLifeRegistry.start()
            Log.i(TAG, "Start")
        }
    }

    override fun onStop() {
        super.onStop()
        if (isVisibleOnScreen()) {
            performStopFragment()
            mLifeRegistry.stop()
            Log.i(TAG, "Stop")
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVisibleOnScreen()) {
            mLifeRegistry.resume()
            Log.i(TAG, "Resume")
        }
    }

    override fun onPause() {
        super.onPause()
        if (isVisibleOnScreen()) {
            mLifeRegistry.pause()
            Log.i(TAG, "Pause")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            mLifeRegistry.pause().stop()
            performStopFragment()
            Log.i(TAG, "Hide")
        } else {
            mLifeRegistry.start()
            performStartFragment()
            mLifeRegistry.resume()
            Log.i(TAG, "Show")
        }
        dispatchHidden(hidden)
    }

    private fun performStopFragment() {
        (activity as? BaseActivity)?.also { (it.resultLife as ResultRegistry).backPresses.remove(this) }
        onFragmentStopped()
    }

    private fun performStartFragment() {
        (activity as? BaseActivity)?.also { (it.resultLife as ResultRegistry).backPresses.add(this) }
        onFragmentStarted()
        arguments?.apply { onNavigateArguments(this) }
    }

    protected open fun onFragmentStarted() {
    }

    protected open fun onFragmentStopped() {
    }

    open fun onNavigateArguments(bundle: Bundle) {
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