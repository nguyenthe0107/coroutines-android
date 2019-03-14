package android.support.core.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.core.annotations.SharedOf
import android.support.core.base.BaseActivity
import android.support.core.base.BaseFragment
import android.support.core.factory.ViewModelFactory
import android.support.core.lifecycle.ResultLifecycle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import java.io.Serializable
import kotlin.reflect.KClass

const val REQUEST_FOR_RESULT_INSTANTLY = 1000

fun BaseFragment.open(clazz: KClass<out AppCompatActivity>, vararg args: Pair<String, Serializable>): BaseFragment {
    startActivity(Intent(activity, clazz.java).apply { args.forEach { putExtra(it.first, it.second) } })
    return this
}

fun BaseActivity.open(clazz: KClass<out AppCompatActivity>, vararg args: Pair<String, Serializable>): BaseActivity {
    startActivity(Intent(this, clazz.java).apply { args.forEach { putExtra(it.first, it.second) } })
    return this
}

fun BaseFragment.openForResult(clazz: KClass<out AppCompatActivity>, vararg args: Pair<String, Serializable>): ResultLifecycle {
    startActivityForResult(Intent(activity, clazz.java).apply {
        args.forEach { putExtra(it.first, it.second) }
    }, REQUEST_FOR_RESULT_INSTANTLY)
    return resultLife
}

fun BaseActivity.openForResult(
    clazz: KClass<out AppCompatActivity>,
    vararg args: Pair<String, Serializable>
): ResultLifecycle {
    startActivityForResult(Intent(this, clazz.java).apply {
        args.forEach { putExtra(it.first, it.second) }
    }, REQUEST_FOR_RESULT_INSTANTLY)
    return resultLife
}

fun FragmentActivity.close() {
    finish()
}

inline fun <reified T : ViewModel> Fragment.viewModel(sharedOf: SharedOf) =
    lazy {
        val factory = ViewModelFactory.sInstance
        val provider = when (sharedOf) {
            SharedOf.NONE -> ViewModelProviders.of(this, factory)
            SharedOf.PARENT -> {
                if (parentFragment != null)
                    ViewModelProviders.of(parentFragment!!, factory)
                else
                    ViewModelProviders.of(activity!!, factory)
            }
            else -> ViewModelProviders.of(activity!!, factory)
        }
        provider.get(T::class.java)
    }

inline fun <reified T : ViewModel> FragmentActivity.viewModel() = lazy {
    val factory = ViewModelFactory.sInstance
    ViewModelProviders.of(this, factory).get(T::class.java)
}