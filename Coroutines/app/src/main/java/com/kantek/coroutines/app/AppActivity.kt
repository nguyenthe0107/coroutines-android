package com.kantek.coroutines.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.base.BaseActivity
import android.support.core.base.BaseViewModel
import android.support.core.extensions.getAnnotation
import android.support.core.extensions.getFirstGenericParameter
import android.support.core.extensions.observe
import android.support.core.factory.ViewModelFactory
import android.support.core.utils.DriverUtils
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.kantek.coroutines.R
import com.kantek.coroutines.exceptions.AlertException
import com.kantek.coroutines.exceptions.ResourceException
import com.kantek.coroutines.exceptions.SnackException
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
            is ResourceException -> Toast.makeText(this, error.resource, Toast.LENGTH_SHORT).show()
            is SnackException -> Snackbar.make(rootView, error.resource, Snackbar.LENGTH_SHORT).show()
            is AlertException -> mAlertDialog.apply { setMessage(getString(error.resource)) }.show()
            is UnknownHostException -> if (DriverUtils.isNetworkEnabled(this))
                Toast.makeText(this, "Error Internal Server", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, error!!.message, Toast.LENGTH_SHORT).show()
        }
    }
}