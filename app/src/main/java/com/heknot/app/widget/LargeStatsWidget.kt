package com.heknot.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.heknot.app.MainActivity
import com.heknot.app.data.local.database.HeknotDatabase
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Widget grande (4x2) - Dashboard completo con todas las estadísticas
 */
class LargeStatsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HeknotDatabase.getDatabase(context)
        val repository = com.heknot.app.data.repository.OfflineHeknotRepository(database)
        
        val profile = repository.getUserProfile().first()
        val latestWeight = repository.getAllWeights().first().firstOrNull()
        val weight = latestWeight?.weight ?: profile?.currentWeight ?: 0.0f
        
        // Calculate streak
        val allLogs = repository.getAllWeights().first()
        val streak = calculateStreak(allLogs.map { it.dateTime.toLocalDate() })
        
        // Get today's data
        val today = LocalDate.now()
        val caloriesConsumed = repository.getTotalCaloriesConsumedByDate(today).first() ?: 0
        val caloriesBurned = repository.getTotalCaloriesBurnedByDate(today).first() ?: 0
        val balance = caloriesConsumed - caloriesBurned
        
        val waterMl = repository.getTotalWaterByDate(today).first() ?: 0
        val workoutCount = repository.getWorkoutCountByDate(today)
        
        provideContent {
            GlanceTheme {
                LargeStatsContent(weight, streak, balance, waterMl, workoutCount)
            }
        }
    }

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val sortedDates = dates.sorted().reversed()
        var streak = 0
        var expectedDate = LocalDate.now()
        for (date in sortedDates) {
            val daysDiff = ChronoUnit.DAYS.between(date, expectedDate)
            if (daysDiff <= 1) {
                streak++
                expectedDate = date.minusDays(1)
            } else break
        }
        return streak
    }

    @Composable
    private fun LargeStatsContent(
        weight: Float,
        streak: Int,
        calorieBalance: Int,
        waterMl: Int,
        workoutCount: Int
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(20.dp)
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            // Header with branding and streak
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Heknot",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    )
                )
                if (streak > 0) {
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    val flames = when {
                        streak >= 30 -> "🔥🔥🔥"
                        streak >= 14 -> "🔥🔥"
                        else -> "🔥"
                    }
                    Text(
                        text = "$flames $streak",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.error
                        )
                    )
                }
            }
            
            Spacer(modifier = GlanceModifier.height(12.dp))
            
            // Main stats row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Weight
                StatCard(
                    modifier = GlanceModifier.defaultWeight(),
                    label = "Peso",
                    value = String.format("%.1f", weight),
                    unit = "kg",
                    emoji = "⚖️"
                )
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                // Calorie Balance
                StatCard(
                    modifier = GlanceModifier.defaultWeight(),
                    label = "Balance",
                    value = "${if (calorieBalance > 0) "+" else ""}$calorieBalance",
                    unit = "kcal",
                    emoji = if (calorieBalance > 0) "🔴" else "🟢",
                    valueColor = if (calorieBalance > 0) 
                        GlanceTheme.colors.error 
                    else 
                        GlanceTheme.colors.primary
                )
            }
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Secondary stats row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Water
                MiniStatCard(
                    modifier = GlanceModifier.defaultWeight(),
                    emoji = "💧",
                    value = "${waterMl}ml"
                )
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                // Workouts
                MiniStatCard(
                    modifier = GlanceModifier.defaultWeight(),
                    emoji = "💪",
                    value = "$workoutCount ${if (workoutCount == 1) "ejercicio" else "ejercicios"}"
                )
            }
        }
    }

    @Composable
    private fun StatCard(
        modifier: GlanceModifier = GlanceModifier,
        label: String,
        value: String,
        unit: String,
        emoji: String,
        valueColor: androidx.glance.unit.ColorProvider = GlanceTheme.colors.onSurface
    ) {
        Column(
            modifier = modifier
                .background(GlanceTheme.colors.surfaceVariant)
                .cornerRadius(12.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = valueColor
                    )
                )
                Spacer(modifier = GlanceModifier.width(2.dp))
                Text(
                    text = unit,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    ),
                    modifier = GlanceModifier.padding(bottom = 2.dp)
                )
            }
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }

    @Composable
    private fun MiniStatCard(
        modifier: GlanceModifier = GlanceModifier,
        emoji: String,
        value: String
    ) {
        Row(
            modifier = modifier
                .background(GlanceTheme.colors.surfaceVariant)
                .cornerRadius(8.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = TextStyle(fontSize = 14.sp)
            )
            Spacer(modifier = GlanceModifier.width(4.dp))
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}
