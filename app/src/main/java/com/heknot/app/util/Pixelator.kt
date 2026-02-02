package com.heknot.app.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

object Pixelator {

    suspend fun pixelate(bitmap: Bitmap, pixelSize: Int): ImageBitmap = withContext(Dispatchers.Default) {
        if (pixelSize <= 1) return@withContext bitmap.asImageBitmap()

        val width = bitmap.width
        val height = bitmap.height

        // Calculate the number of pixels in the downscaled image
        val downscaledWidth = (width / pixelSize).coerceAtLeast(1)
        val downscaledHeight = (height / pixelSize).coerceAtLeast(1)

        // Downscale
        val downscaledBitmap = Bitmap.createScaledBitmap(bitmap, downscaledWidth, downscaledHeight, false)
        
        // Upscale back to original size (nearest neighbor is default for createScaledBitmap with filter=false)
        val pixelatedBitmap = Bitmap.createScaledBitmap(downscaledBitmap, width, height, false)

        downscaledBitmap.recycle() // Clean up intermediate bitmap
        
        return@withContext pixelatedBitmap.asImageBitmap()
    }
}
