package com.heknot.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenterResult
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.framework.image.ByteBufferExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GenerativePixelArtGenerator(private val context: Context) {

    private var imageSegmenter: ImageSegmenter? = null
    private var ganInterpreter: Interpreter? = null

    init {
        setupSegmenter()
        setupGan()
    }

    private fun setupSegmenter() {
        try {
            val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("selfie_segmenter.tflite")
            val optionsBuilder = ImageSegmenter.ImageSegmenterOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setRunningMode(RunningMode.IMAGE)
                .setOutputCategoryMask(true)
                .build()
            imageSegmenter = ImageSegmenter.createFromOptions(context, optionsBuilder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupGan() {
        try {
            // Placeholder: Assuming pix2pix_pixelart.tflite exists in assets
            val modelBuffer = context.assets.open("pix2pix_pixelart.tflite").use { 
                val buffer = ByteBuffer.allocateDirect(it.available())
                buffer.order(ByteOrder.nativeOrder())
                val bytes = it.readBytes()
                buffer.put(bytes)
                buffer.rewind()
                buffer
            }
            ganInterpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Removes the background from a food photo using MediaPipe.
     */
    suspend fun removeBackground(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val segmenter = imageSegmenter ?: return@withContext bitmap
        
        val mpImage = BitmapImageBuilder(bitmap).build()
        val result: ImageSegmenterResult = segmenter.segment(mpImage)
        val maskMpImage = result.categoryMask().get()
        
        // Correct way to extract ByteBuffer from MPImage in MediaPipe Android
        val maskBuffer = ByteBufferExtractor.extract(maskMpImage)
        maskBuffer.rewind()
        
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (i in pixels.indices) {
            val maskVal = maskBuffer.get().toInt() and 0xFF
            // In many segmentation models, 0 is background
            if (maskVal == 0) {
                pixels[i] = Color.TRANSPARENT
            }
        }
        
        return@withContext Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    /**
     * Generates a brand new pixel art image (32x32 or 64x64) from an input photo.
     */
    suspend fun generatePixelArt(bitmap: Bitmap, outputSize: Int = 64): Bitmap = withContext(Dispatchers.Default) {
        val interpreter = ganInterpreter ?: return@withContext simulateGenerativePixelArt(bitmap, outputSize)

        // 1. Manual Preprocess (Resizing + Normalization)
        // Instead of TFLite Support ResizeOp/NormalizeOp, we do it manually to fix manifest conflicts
        val resized = Bitmap.createScaledBitmap(bitmap, outputSize, outputSize, true)
        val inputBuffer = ByteBuffer.allocateDirect(1 * outputSize * outputSize * 3 * 4) // BHWC, Float32
        inputBuffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(outputSize * outputSize)
        resized.getPixels(pixels, 0, outputSize, 0, 0, outputSize, outputSize)
        
        for (pixel in pixels) {
            inputBuffer.putFloat((Color.red(pixel) - 127.5f) / 127.5f)
            inputBuffer.putFloat((Color.green(pixel) - 127.5f) / 127.5f)
            inputBuffer.putFloat((Color.blue(pixel) - 127.5f) / 127.5f)
        }
        inputBuffer.rewind()

        // 2. Output Buffer
        val outputBuffer = ByteBuffer.allocateDirect(1 * outputSize * outputSize * 3 * 4) // BHWC, Float32
        outputBuffer.order(ByteOrder.nativeOrder())

        // 3. Run Inference
        interpreter.run(inputBuffer, outputBuffer)

        // 4. Post-process
        outputBuffer.rewind()
        val resultBitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
        for (y in 0 until outputSize) {
            for (x in 0 until outputSize) {
                val r = ((outputBuffer.getFloat() + 1f) * 127.5f).toInt().coerceIn(0, 255)
                val g = ((outputBuffer.getFloat() + 1f) * 127.5f).toInt().coerceIn(0, 255)
                val b = ((outputBuffer.getFloat() + 1f) * 127.5f).toInt().coerceIn(0, 255)
                resultBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }

        return@withContext resultBitmap
    }

    private fun simulateGenerativePixelArt(bitmap: Bitmap, size: Int): Bitmap {
        val scaled = Bitmap.createScaledBitmap(bitmap, size, size, false)
        val pixels = IntArray(size * size)
        scaled.getPixels(pixels, 0, size, 0, 0, size, size)
        
        for (i in pixels.indices) {
            val color = pixels[i]
            if (Color.alpha(color) < 128) {
                pixels[i] = Color.TRANSPARENT
                continue
            }
            val r = (Color.red(color) / 64) * 64 + 31
            val g = (Color.green(color) / 64) * 64 + 31
            val b = (Color.blue(color) / 64) * 64 + 31
            pixels[i] = Color.rgb(r, g, b)
        }
        
        return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
    }
}
