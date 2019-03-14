@file:Suppress("UNCHECKED_CAST")

package android.support.core.extensions

import android.content.Intent
import android.support.v4.app.FragmentActivity
import java.io.Serializable

fun <T : Serializable> Intent.get(key: String): T? {
    return this.getSerializableExtra(key) as? T
}

fun <T : Serializable> T.asArgument() = Pair<String, Serializable>(javaClass.name, this)

inline fun <reified T : Serializable> FragmentActivity.argument(): Lazy<T> =
    lazy { intent.getSerializableExtra(T::class.java.name) as T }

fun <T : Serializable> FragmentActivity.argument(key: String): Lazy<T> =
    lazy { intent.getSerializableExtra(key) as T }
