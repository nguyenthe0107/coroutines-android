package com.kantek.coroutines.app

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.annotations.ShareViewModel
import android.support.core.annotations.SharedOf
import android.support.core.base.BaseFragment
import android.support.core.base.BaseViewModel
import android.support.core.extensions.getAnnotation
import android.support.core.extensions.getFirstGenericParameter
import android.support.core.extensions.observe
import android.support.core.factory.ViewModelFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.kantek.coroutines.R

abstract class AppFragment<VM : BaseViewModel> : BaseFragment() {
    lateinit var viewModel: VM
    private var mLoadingView: View? = null

    val appActivity get() = activity as AppActivity<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = when (getAnnotation<ShareViewModel>()?.value) {
            SharedOf.ACTIVITY -> ViewModelProvider(activity!!, ViewModelFactory.sInstance)
            SharedOf.PARENT -> ViewModelProvider(parentFragment!!, ViewModelFactory.sInstance)
            else -> ViewModelProvider(this, ViewModelFactory.sInstance)
        }.get(getFirstGenericParameter()) as VM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingView = view.findViewById(R.id.viewLoading)
        if (viewModel != (activity as? AppActivity<*>)?.viewModel) {
            viewModel.loading.observe(this, this::showLoading)
            viewModel.error.observe(this, appActivity::handleError)
        }
    }

    private fun showLoading(it: Boolean?) {
        mLoadingView?.visibility = if (it!!) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getAnnotation<LayoutId>()!!.value, container, false)
    }

    fun toast(@StringRes res: Int) = appActivity.toast(res)

    fun toast(text: String) = appActivity.toast(text)
}
