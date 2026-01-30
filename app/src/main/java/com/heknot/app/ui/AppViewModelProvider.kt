package com.heknot.app.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.heknot.app.HeknotApplication
import com.heknot.app.ui.screens.chart.ChartViewModel
import com.heknot.app.ui.screens.home.HomeViewModel
import com.heknot.app.ui.screens.history.HistoryViewModel
import com.heknot.app.ui.screens.nutrition.NutritionViewModel
import com.heknot.app.ui.screens.onboarding.OnboardingViewModel
import com.heknot.app.ui.screens.settings.SettingsViewModel
import com.heknot.app.ui.screens.workout.WorkoutViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for OnboardingViewModel
        initializer {
            OnboardingViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }
        
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }

        // Initializer for WorkoutViewModel
        initializer {
            WorkoutViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }

        // Initializer for ChartViewModel
        initializer {
            ChartViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }

        // Initializer for MainViewModel
        initializer {
            MainViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }
        
        // Initializer for SettingsViewModel
        initializer {
            SettingsViewModel(
                HeknotApplication().container.HeknotRepository,
                HeknotApplication().container.backupManager
            )
        }

        // Initializer for HistoryViewModel
        initializer {
            HistoryViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }

        // Initializer for NutritionViewModel
        initializer {
            NutritionViewModel(
                HeknotApplication().container.HeknotRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [HeknotApplication].
 */
fun CreationExtras.HeknotApplication(): HeknotApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HeknotApplication)
