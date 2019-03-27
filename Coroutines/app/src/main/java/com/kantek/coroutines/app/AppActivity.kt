package com.kantek.coroutines.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.core.annotations.LayoutId
import android.support.core.base.BaseActivity
import android.support.core.base.BaseViewModel
import android.support.core.extensions.getAnnotation
import android.support.core.extensions.getFirstGenericParameter
import android.support.core.extensions.inject
import android.support.core.extensions.observe
import android.support.core.factory.ViewModelFactory
import android.support.core.utils.DriverUtils
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.kantek.coroutines.R
import com.kantek.coroutines.datasource.AppEvent
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class AppActivity<VM : BaseViewModel> : BaseActivity() {
    lateinit var rootView: View
    private val mAlertDialog: AlertDialog by lazy {
        AlertDialog.Builder(this)
            .setPositiveButton("Ok", null)
            .setCancelable(true)
            .create()
    }
    private val mLoadingView: View? by lazy { findViewById<View>(R.id.viewLoading) }
    val appEvent: AppEvent by inject()

    @Suppress("UNCHECKED_CAST")
    val viewModel: VM by lazy {
        ViewModelProviders
            .of(this, ViewModelFactory.sInstance)
            .get(getFirstGenericParameter()) as VM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAnnotation<LayoutId>()?.apply { setContentView(value) }
        rootView = findViewById<View>(android.R.id.content)
        viewModel.loading.observe(this) { showLoading(it) }
        viewModel.error.observe(this, this::handleError)
    }

    fun showLoading(it: Boolean?) {
        mLoadingView?.visibility = if (it!!) View.VISIBLE else View.GONE
    }

    fun handleError(error: Throwable?) {
        when (error) {
            is ResourceException -> toast(error.resource)
            is SocketTimeoutException -> toast(R.string.error_request_timeout)
            is SnackException -> Snackbar.make(rootView, error.resource, Snackbar.LENGTH_SHORT).show()
            is AlertException -> mAlertDialog.apply { setMessage(getString(error.resource)) }.show()
            is UnknownHostException -> if (DriverUtils.isNetworkEnabled(this))
                toast("Error Internal Server")
            else toast("No network connection")
            else -> toast(error?.message ?: "Unknown")
        }
    }

    fun toast(@StringRes res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}