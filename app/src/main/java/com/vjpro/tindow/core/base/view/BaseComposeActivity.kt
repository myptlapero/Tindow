package com.vjpro.tindow.core.base.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.vjpro.tindow.core.extension.hideNavigationBar
import com.vjpro.tindow.core.extension.hideSystemBars

/**
 * Base Compose activity — no theme dependency.
 * Handles edge-to-edge, status bar, navigation bar hiding.
 * Subclasses provide [ThemeContent] to wrap [ContentView] with an app-specific theme.
 */
abstract class BaseComposeActivity : AppCompatActivity() {

    open fun isHideSystemBar(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        setContent {
            ThemeContent {
                ContentView()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isHideSystemBar()) {
            window.hideSystemBars()
        } else {
            window.hideNavigationBar()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isHideSystemBar()) {
            window.hideSystemBars()
        } else {
            window.hideNavigationBar()
        }
    }

    /** Override to wrap content with an app-specific theme. Default = no theme wrapper. */
    @Composable
    protected open fun ThemeContent(content: @Composable () -> Unit) {
        content()
    }

    @Composable
    protected abstract fun ContentView()
}
