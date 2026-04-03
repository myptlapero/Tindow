package com.vjpro.tindow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vjpro.tindow.core.base.view.BaseComposeActivity
import com.vjpro.tindow.ui.theme.TindowTheme

class MainActivity : BaseComposeActivity() {

    @Composable
    override fun ThemeContent(content: @Composable () -> Unit) {
        TindowTheme { content() }
    }

    @Composable
    override fun ContentView() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TindowTheme {
        Greeting("Android")
    }
}
