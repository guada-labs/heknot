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
 * Widget de racha (2x1) - Muestra racha con llamas según milestone
 */
class StreakWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HeknotDatabase.getDatabase(context)
        val repository = com.heknot.app.data.repository.OfflineHeknotRepository(database)
        
        val allLogs = repository.getAllWeights().first()
        val streak = calculateStreak(allLogs.map { it.dateTime.toLocalDate() })
        
        provideContent {
            GlanceTheme {
                StreakContent(streak)
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
    private fun StreakContent(streak: Int) {
        // Flames based on milestones
        val flames = when {
            streak >= 30 -> "🔥🔥🔥" // 30+ días
            streak >= 14 -> "🔥🔥"   // 2+ semanas
            streak >= 7 -> "🔥"      // 1+ semana
            streak > 0 -> "🔥"       // Cualquier racha
            else -> "💪"             // Sin racha
        }
        
        val message = when {
            streak >= 30 -> "¡Imparable!"
            streak >= 14 -> "¡Increíble!"
            streak >= 7 -> "¡Excelente!"
            streak > 0 -> "¡Sigue así!"
            else -> "¡Empieza hoy!"
        }
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.errorContainer)
                .cornerRadius(16.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            if (streak > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flames
                    Text(
                        text = flames,
                        style = TextStyle(fontSize = 24.sp)
                    )
                    
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    
                    // Streak number
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$streak",
                            style = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlanceTheme.colors.onErrorContainer
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Text(
                            text = if (streak == 1) "día" else "días",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = GlanceTheme.colors.onErrorContainer
                            ),
                            modifier = GlanceModifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    // Motivational message
                    Text(
                        text = message,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = GlanceTheme.colors.onErrorContainer
                        )
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = flames,
                        style = TextStyle(fontSize = 32.sp)
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = message,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onErrorContainer
                        )
                    )
                }
            }
        }
    }
}
