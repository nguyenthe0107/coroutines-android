@file:Suppress("UNCHECKED_CAST")

package android.support.core.extensions

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import java.io.Serializable

fun <T : Any> Intent.get(key: String): T? {
    return extras?.get(key) as? T
}

fun <T : Any> Bundle.get(key: String): T? {
    return get(key) as? T
}

inline fun <reified T : Any> Bundle.get(): T? {
    return get(T::class.java.name) as? T
}

inline fun <reified T : Any> Intent.get(): T? {
    return extras?.get(T::class.java.name) as? T
}

fun <B : Serializable> Pair<String, B>.toBundle() =
    Bundle().apply { putSerializable(first, second) }

fun <T : Serializable> T.asArgument() = Pair<String, Serializable>(javaClass.name, this)
fun <T : Parcelable> T.asArgument() = Pair<String, Parcelable>(javaClass.name, this)

fun <T : Serializable> T.asArgument(key: String) = Pair<String, Serializable>(key, this)
fun <T : Parcelable> T.asArgument(key: String) = Pair<String, Parcelable>(javaClass.name, this)

inline fun <reified T : Any> FragmentActivity.argument(): Lazy<T> =
    lazy { intent.extras?.get(T::class.java.name) as T }

fun <T : Any> FragmentActivity.argument(key: String): Lazy<T> =
    lazy { intent.extras?.get(key) as T }
