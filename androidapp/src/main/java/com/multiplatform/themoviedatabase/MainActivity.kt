package com.multiplatform.themoviedatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.multiplatform.data.datasource.local.DatabaseDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.enableEdgeToEdge(window)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true
        super.onCreate(savedInstanceState)
        val driverFactory = DatabaseDriverFactory(applicationContext)
        setContent {
            App(driverFactory = driverFactory)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val context: Context = LocalContext.current
    App(driverFactory = DatabaseDriverFactory(context))
}