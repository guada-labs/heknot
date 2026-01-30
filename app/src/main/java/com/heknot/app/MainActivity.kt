package com.heknot.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.heknot.app.ui.MainViewModel
import com.heknot.app.ui.AppViewModelProvider
import com.heknot.app.ui.HeknotApp
import com.heknot.app.security.BiometricAuthenticator
import com.heknot.app.ui.theme.HeknotTheme

class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels { AppViewModelProvider.Factory }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val biometricAuthenticator = BiometricAuthenticator(this)

        setContent {
            val biometricEnabled by viewModel.biometricEnabled.collectAsState()
            val isAuthenticated by viewModel.isAuthenticated.collectAsState()
            val userDarkModePref by viewModel.isDarkMode.collectAsState()
            val isDarkMode = userDarkModePref ?: isSystemInDarkTheme()

            HeknotTheme(darkTheme = isDarkMode) {
                if (biometricEnabled && !isAuthenticated) {
                    // Minimalist lock screen while authenticating
                    com.heknot.app.ui.screens.lock.LockScreen(
                        onAuthenticate = {
                            biometricAuthenticator.authenticate(
                                onSuccess = { viewModel.setAuthenticated(true) },
                                onError = { /* Handle error */ },
                                onFailed = { /* Handle failure */ }
                            )
                        }
                    )
                } else {
                    HeknotApp(viewModel = viewModel)
                }
            }
        }
    }
}

