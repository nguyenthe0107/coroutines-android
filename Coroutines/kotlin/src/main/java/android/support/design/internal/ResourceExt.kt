package android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.MenuRes
import android.support.v7.view.menu.MenuBuilder
import android.view.Menu
import android.view.MenuInflater

@SuppressLint("RestrictedApi")
fun Context.getMenu(@MenuRes id: Int): Menu {
    val menu = MenuBuilder(this)
    MenuInflater(this).inflate(id, menu)
    return menu
}
