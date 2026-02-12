package com.heknot.app.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.heknot.app.HeknotApplication
import com.heknot.app.ui.screens.chart.ChartViewModel
import com.heknot.app.ui.screens.home.HomeViewModel
import com.heknot.app.ui.screens.history.HistoryViewModel
import com.heknot.app.ui.screens.nutrition.NutritionViewModel
import com.heknot.app.ui.screens.onboarding.OnboardingViewModel
import com.heknot.app.ui.screens.settings.SettingsViewModel
import com.heknot.app.ui.screens.workout.WorkoutViewModel
import com.heknot.app.ui.screens.water.WaterViewModel
import com.heknot.app.ui.screens.profile.ProfileViewModel
import com.heknot.app.ui.screens.training.equipment.EquipmentViewModel
import com.heknot.app.ui.screens.training.catalog.PlanCatalogViewModel
import com.heknot.app.ui.screens.training.detail.WorkoutDetailViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for OnboardingViewModel
        initializer {
            val app = heknotApplication()
            OnboardingViewModel(
                repository = app.container.HeknotRepository,
                analyst = com.heknot.app.util.GeminiOnboardingAnalyst(app.applicationContext)
            )
        }
        
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for WorkoutViewModel
        initializer {
            WorkoutViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for ChartViewModel
        initializer {
            ChartViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for MainViewModel
        initializer {
            MainViewModel(
                heknotApplication().container.HeknotRepository
            )
        }
        
        // Initializer for SettingsViewModel
        initializer {
            val app = heknotApplication()
            SettingsViewModel(
                app.container.HeknotRepository,
                app.container.backupManager
            )
        }

        // Initializer for HistoryViewModel
        initializer {
            HistoryViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for NutritionViewModel
        initializer {
            NutritionViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for WaterViewModel
        initializer {
            WaterViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for ProfileViewModel
        initializer {
            ProfileViewModel(
                heknotApplication().container.HeknotRepository
            )
        }

        // Initializer for EquipmentViewModel
        initializer {
            val app = heknotApplication()
            EquipmentViewModel(
                trainingRepository = app.container.trainingRepository
            )
        }

        // Initializer for PlanCatalogViewModel
        initializer {
            PlanCatalogViewModel(
                trainingRepository = heknotApplication().container.trainingRepository
            )
        }

        // Initializer for WorkoutDetailViewModel
        initializer {
            WorkoutDetailViewModel(
                savedStateHandle = createSavedStateHandle(),
                trainingRepository = heknotApplication().container.trainingRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [HeknotApplication].
 */
fun CreationExtras.heknotApplication(): HeknotApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HeknotApplication)
