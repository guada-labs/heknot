package com.fittrack.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun AnimatedStreakBadge(streak: Int) {
    if (streak <= 0) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        StylizedParticleFlame(modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$streak",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StylizedParticleFlame(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame_system")

    // --- Animaciones de la Llama ---
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)),
        label = "phase"
    )

    // --- Animación de Partículas (Sparks) ---
    val particleProgress = List(5) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200 + (i * 150), easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "p$i"
        )
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val baseLineY = h * 0.85f // Base común para todas las capas

        // 1. DIBUJAR CAPAS DE LLAMA (COLORES SÓLIDOS ALINEADOS A LA BASE)
        fun drawFlameLayer(widthScale: Float, heightScale: Float, color: Color, xOffset: Float) {
            val fw = w * widthScale
            val fh = h * heightScale
            val path = Path().apply {
                // Empezar en la punta
                moveTo(centerX + xOffset, baseLineY - fh)
                
                // Curva derecha bajando hasta la base central
                cubicTo(
                    centerX + fw * 0.6f + xOffset, baseLineY - fh * 0.4f,
                    centerX + fw * 0.4f + xOffset, baseLineY,
                    centerX + xOffset, baseLineY
                )
                
                // Curva izquierda subiendo hasta la punta
                cubicTo(
                    centerX - fw * 0.4f + xOffset, baseLineY,
                    centerX - fw * 0.6f + xOffset, baseLineY - fh * 0.4f,
                    centerX + xOffset, baseLineY - fh
                )
            }
            drawPath(path = path, color = color)
        }

        // Capa Exterior (Roja) - La más grande
        drawFlameLayer(
            widthScale = 0.7f,
            heightScale = 0.8f + (sin(phase) * 0.05f),
            color = Color(0xFFF44336), // Rojo
            xOffset = sin(phase) * 2f
        )

        // Capa Media (Naranja) - Centrada y compartiendo base
        drawFlameLayer(
            widthScale = 0.5f,
            heightScale = 0.55f + (sin(phase * 1.5f) * 0.03f),
            color = Color(0xFFFF9800), // Naranja
            xOffset = sin(phase * 1.2f) * -1.5f
        )

        // Capa Interior (Amarilla) - Pequeña pero en la misma base
        drawFlameLayer(
            widthScale = 0.3f,
            heightScale = 0.3f + (sin(phase * 2f) * 0.02f),
            color = Color(0xFFFFEB3B), // Amarillo
            xOffset = sin(phase * 2f) * 1f
        )

        // 2. SISTEMA DE PARTÍCULAS (ALINEADAS AL FLUJO)
        particleProgress.forEachIndexed { index, progress ->
            val pX = centerX + sin(phase + index) * (w * 0.25f)
            val pY = baseLineY - (progress.value * h * 0.9f)
            val pAlpha = 1f - progress.value
            val pSize = (1f - progress.value) * 5f

            // Solo mostrar si están por encima de la base de la llama
            if (pY < baseLineY - 10f) {
                drawCircle(
                    color = if (index % 2 == 0) Color(0xFFFFEB3B) else Color(0xFFFF9800),
                    radius = pSize,
                    center = Offset(pX, pY),
                    alpha = pAlpha
                )
            }
        }
    }
}
