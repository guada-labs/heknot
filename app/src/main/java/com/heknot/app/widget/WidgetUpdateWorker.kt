package com.heknot.app.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(HeknotWidget::class.java)
            
            glanceIds.forEach { glanceId ->
                HeknotWidget().update(context, glanceId)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
