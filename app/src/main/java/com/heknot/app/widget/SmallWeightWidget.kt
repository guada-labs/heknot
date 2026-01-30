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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.heknot.app.MainActivity
import com.heknot.app.data.local.database.HeknotDatabase
import kotlinx.coroutines.flow.first

/**
 * Widget pequeño (2x1) - Peso actual con tendencia
 */
class SmallWeightWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = HeknotDatabase.getDatabase(context)
        val repository = com.heknot.app.data.repository.OfflineHeknotRepository(database)
        
        val profile = repository.getUserProfile().first()
        val weights = repository.getAllWeights().first()
        val currentWeight = weights.firstOrNull()?.weight ?: profile?.currentWeight ?: 0.0f
        
        // Calculate trend (last 2 weights)
        val trend = if (weights.size >= 2) {
            val diff = weights[0].weight - weights[1].weight
            when {
                diff < -0.1f -> "↓" // Bajando
                diff > 0.1f -> "↑"  // Subiendo
                else -> "→"         // Estable
            }
        } else "→"
        
        provideContent {
            GlanceTheme {
                SmallWeightContent(currentWeight, trend)
            }
        }
    }

    @Composable
    private fun SmallWeightContent(weight: Float, trend: String) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(16.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trend indicator
                Text(
                    text = trend,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )
                
                // Weight
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%.1f", weight),
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = "kg",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = GlanceTheme.colors.onPrimaryContainer
                        ),
                        modifier = GlanceModifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}
