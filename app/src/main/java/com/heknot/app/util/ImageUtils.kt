package com.heknot.app.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

object ImageUtils {

    /**
     * Enhances a bitmap for better OCR results.
     * Increases contrast and converts to grayscale (optional but helpful for some engines).
     */
    fun enhanceForOcr(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val enhancedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val canvas = Canvas(enhancedBitmap)
        val paint = Paint()

        // 1. Contrast Adjustment
        // We'll use a color matrix to boost contrast and slightly brighten
        val contrast = 1.5f // 1.0 is normal
        val brightness = 10f // 0 is normal
        
        val colorMatrix = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        
        // 2. Grayscale (Optional but often helps OCR engines focus on shapes)
        // Uncomment if needed:
        // colorMatrix.setSaturation(0f)

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // Step 2: Adaptive Normalization (CLAHE-Lite)
        val normalized = applyClaheLite(enhancedBitmap)

        return binarize(normalized)
    }

    /**
     * Contrast-Limited Adaptive Histogram Equalization (Simplified Approach)
     * Divides image into tiles and normalizes contrast locally to handle shadows/glare.
     */
    private fun applyClaheLite(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val tileSize = 64
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        // Process in tiles to equalize lighting
        for (ty in 0 until height step tileSize) {
            for (tx in 0 until width step tileSize) {
                val currentTileWidth = if (tx + tileSize > width) width - tx else tileSize
                val currentTileHeight = if (ty + tileSize > height) height - ty else tileSize
                
                // Find min/max luminance in tile
                var minLum = 255
                var maxLum = 0
                
                for (y in ty until ty + currentTileHeight) {
                    for (x in tx until tx + currentTileWidth) {
                        val color = pixels[y * width + x]
                        val lum = (Color.red(color) * 0.3 + Color.green(color) * 0.59 + Color.blue(color) * 0.11).toInt()
                        if (lum < minLum) minLum = lum
                        if (lum > maxLum) maxLum = lum
                    }
                }
                
                // Normalize tile contrast
                val range = (maxLum - minLum).coerceAtLeast(1)
                for (y in ty until ty + currentTileHeight) {
                    for (x in tx until tx + currentTileWidth) {
                        val color = pixels[y * width + x]
                        val r = (((Color.red(color) - minLum).toFloat() / range) * 255).toInt().coerceIn(0, 255)
                        val g = (((Color.green(color) - minLum).toFloat() / range) * 255).toInt().coerceIn(0, 255)
                        val b = (((Color.blue(color) - minLum).toFloat() / range) * 255).toInt().coerceIn(0, 255)
                        pixels[y * width + x] = Color.rgb(r, g, b)
                    }
                }
            }
        }
        
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    /**
     * Converts a bitmap to high-contrast Black and White using local adaptive thresholding concept.
     */
    private fun binarize(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val binarized = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (i in pixels.indices) {
            val color = pixels[i]
            // Calculate luminance
            val luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)).toInt()
            // Simple threshold at 128 (can be improved with Otsu)
            val finalColor = if (luminance < 140) Color.BLACK else Color.WHITE
            pixels[i] = finalColor
        }
        
        binarized.setPixels(pixels, 0, width, 0, 0, width, height)
        return binarized
    }

    /**
     * Rotates a bitmap by specific degrees.
     */
    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Crops a bitmap to a specific rectangular region defined by normalized coordinates (0-1).
     */
    fun cropToRegion(bitmap: Bitmap, left: Float, top: Float, right: Float, bottom: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        val x = (left * width).toInt().coerceIn(0, width - 1)
        val y = (top * height).toInt().coerceIn(0, height - 1)
        val w = ((right - left) * width).toInt().coerceAtMost(width - x)
        val h = ((bottom - top) * height).toInt().coerceAtMost(height - y)
        
        return if (w > 0 && h > 0) {
            Bitmap.createBitmap(bitmap, x, y, w, h)
        } else {
            bitmap
        }
    }

    /**
     * Inverts colors (useful for white-on-black labels).
     */
    fun invert(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val color = pixels[i]
            pixels[i] = Color.rgb(255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color))
        }
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }
}
