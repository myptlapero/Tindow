package com.vjpro.tindow.core.extension

import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/** Hide the navigation bar with swipe-to-reveal behavior. */
fun Window.hideNavigationBar() {
    runCatching {
        val controller = WindowCompat.getInsetsController(this, this.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }.getOrElse {
        println("Failed to hide navigation bar: ${it.message}")
    }
}

/** Hide both status bar and navigation bar with swipe-to-reveal behavior. */
fun Window.hideSystemBars() {
    runCatching {
        val controller = WindowCompat.getInsetsController(this, this.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }.getOrElse {
        println("Failed to hide system bars: ${it.message}")
    }
}
