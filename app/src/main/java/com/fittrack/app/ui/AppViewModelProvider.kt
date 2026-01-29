package com.fittrack.app.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fittrack.app.FitTrackApplication
import com.fittrack.app.ui.screens.chart.ChartViewModel
import com.fittrack.app.ui.screens.home.HomeViewModel
import com.fittrack.app.ui.screens.history.HistoryViewModel
import com.fittrack.app.ui.screens.onboarding.OnboardingViewModel
import com.fittrack.app.ui.screens.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for OnboardingViewModel
        initializer {
            OnboardingViewModel(
                fitTrackApplication().container.fitTrackRepository
            )
        }
        
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                fitTrackApplication().container.fitTrackRepository
            )
        }

        // Initializer for ChartViewModel
        initializer {
            ChartViewModel(
                fitTrackApplication().container.fitTrackRepository
            )
        }

        // Initializer for MainViewModel
        initializer {
            MainViewModel(
                fitTrackApplication().container.fitTrackRepository
            )
        }
        
        // Initializer for SettingsViewModel
        initializer {
            SettingsViewModel(
                fitTrackApplication().container.fitTrackRepository,
                fitTrackApplication().container.backupManager
            )
        }

        // Initializer for HistoryViewModel
        initializer {
            HistoryViewModel(
                fitTrackApplication().container.fitTrackRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [FitTrackApplication].
 */
fun CreationExtras.fitTrackApplication(): FitTrackApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitTrackApplication)
