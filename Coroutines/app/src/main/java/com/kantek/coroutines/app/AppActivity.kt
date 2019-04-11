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
import android.support.core.helpers.AppSettings
import android.support.core.utils.DriverUtils
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.kantek.coroutines.R
import com.kantek.coroutines.datasource.AppEvent
import com.kantek.coroutines.views.LoginActivity
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class AppActivity<VM : BaseViewModel> : BaseActivity() {
    private val mAlertDialog: AlertDialog by lazy {
        AlertDialog.Builder(this)
            .setPositiveButton("Ok", null)
            .setCancelable(true)
            .create()
    }
    lateinit var viewModel: VM
        private set
    private var mLoadingView: View? = null
    lateinit var rootView: View

    val appEvent: AppEvent by inject()
    val appPermission by lazy { AppPermission(this) }
    val appSettings by lazy { AppSettings(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAnnotation<LayoutId>()?.apply { setContentView(value) }
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProviders.of(this, ViewModelFactory.sInstance).get(getFirstGenericParameter()) as VM

        mLoadingView = findViewById(R.id.viewLoading)
        rootView = findViewById(android.R.id.content)

        viewModel.loading.observe(this, this::showLoading)
        viewModel.error.observe(this, this::handleError)
    }

    private fun showLoading(it: Boolean?) {
        mLoadingView?.visibility = if (it!!) View.VISIBLE else View.GONE
    }

    fun handleError(error: Throwable?) {
        when (error) {
            is TokenException -> {
                if (isFinishing) return
                LoginActivity.show(this)
            }
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