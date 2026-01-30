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

class HeknotWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HeknotDatabase.getDatabase(context)
        val repository = com.heknot.app.data.repository.OfflineHeknotRepository(database)
        
        // Get user profile
        val profile = repository.getUserProfile().first()
        
        // Get latest weight
        val latestWeight = repository.getAllWeights().first().firstOrNull()
        
        // Calculate streak
        val allLogs = repository.getAllWeights().first()
        val streak = calculateStreak(allLogs.map { it.dateTime.toLocalDate() })
        
        provideContent {
            GlanceTheme {
                WidgetContent(
                    weight = latestWeight?.weight ?: profile?.currentWeight ?: 0.0f,
                    streak = streak
                )
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
            } else {
                break
            }
        }
        
        return streak
    }

    @Composable
    private fun WidgetContent(weight: Float, streak: Int) {
        // Flames based on milestones
        val flames = when {
            streak >= 30 -> "🔥🔥🔥"
            streak >= 14 -> "🔥🔥"
            streak >= 7 -> "🔥"
            streak > 0 -> "🔥"
            else -> ""
        }
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.secondaryContainer)
                .cornerRadius(16.dp)
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App name with streak
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Heknot",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSecondaryContainer
                    )
                )
                if (flames.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = flames,
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Weight display
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = String.format("%.1f", weight),
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSecondaryContainer
                    )
                )
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    text = "kg",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = GlanceTheme.colors.onSecondaryContainer
                    ),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Streak display
            if (streak > 0) {
                Text(
                    text = "$streak ${if (streak == 1) "día" else "días"} consecutivos",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = GlanceTheme.colors.onSecondaryContainer
                    )
                )
            } else {
                Text(
                    text = "¡Registra tu peso hoy!",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onSecondaryContainer
                    )
                )
            }
        }
    }
}
