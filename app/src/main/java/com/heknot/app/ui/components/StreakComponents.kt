package com.heknot.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.compose.animation.animateColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.GenericShape
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.sin

val ShieldShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.5f, 0f)
    lineTo(w * 1.0f, h * 0.22f)
    lineTo(w * 0.92f, h * 0.72f)
    quadraticTo(w * 0.5f, h * 0.98f, w * 0.08f, h * 0.72f)
    lineTo(0f, h * 0.22f)
    close()
}

@Composable
fun StreakDetailDialog(
    streakInfo: com.heknot.app.ui.screens.home.StreakInfo,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StylizedConsistencyShield(modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(8.dp))
                Text("Escudo de Consistencia")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StreakStat(
                        label = "Racha Actual",
                        value = "${streakInfo.currentStreak}",
                        icon = Icons.Default.Shield,
                        color = MaterialTheme.colorScheme.primary
                    )
                    StreakStat(
                        label = "Mejor Racha",
                        value = "${streakInfo.bestStreak}",
                        icon = Icons.Default.EmojiEvents,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                // Calendar View
                Text(
                    "Días Activos (Este mes)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                ConsistencyCalendar(activeDays = streakInfo.activeDays)

                // Motivational Message
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "¡La disciplina es el puente entre tus metas y tus logros!",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    )
}

@Composable
private fun StreakStat(label: String, value: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ConsistencyCalendar(activeDays: Set<LocalDate>) {
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sunday, 1 = Monday...
    
    val days = (1..daysInMonth).toList()
    val today = LocalDate.now()

    Column {
        // Días de la semana
        Row(Modifier.fillMaxWidth()) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(Modifier.height(4.dp))

        // Grid de días
        var currentDayIndex = 0
        for (week in 0..5) {
            Row(Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val dayNum = if (week == 0 && dayOfWeek < firstDayOfMonth) {
                        null
                    } else if (currentDayIndex < days.size) {
                        days[currentDayIndex++]
                    } else {
                        null
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNum != null) {
                            val date = currentMonth.atDay(dayNum)
                            val isActive = activeDays.contains(date)
                            val isToday = date == today

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (isActive) {
                                    // Escudo premium para días activos
                                    Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
                                        val path = Path().apply {
                                            val w = size.width
                                            val h = size.height
                                            moveTo(w * 0.5f, 0f)
                                            lineTo(w * 1.0f, h * 0.22f)
                                            lineTo(w * 0.92f, h * 0.72f)
                                            quadraticTo(w * 0.5f, h * 0.98f, w * 0.08f, h * 0.72f)
                                            lineTo(0f, h * 0.22f)
                                            close()
                                        }
                                        // Dibujar fondo sutil si es hoy, o sólido si es activo
                                        drawPath(
                                            path = path,
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFFEB3B), Color(0xFFFBC02D))
                                            )
                                        )
                                        // Borde fino
                                        drawPath(
                                            path = path,
                                            color = Color(0xFFD4AF37),
                                            style = Stroke(width = 1.dp.toPx())
                                        )
                                    }
                                } else if (isToday) {
                                    // Borde de escudo para hoy si no es activo todavía
                                    val primaryColor = MaterialTheme.colorScheme.primary
                                    Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
                                        val path = Path().apply {
                                            val w = size.width
                                            val h = size.height
                                            moveTo(w * 0.5f, 0f)
                                            lineTo(w * 1.0f, h * 0.22f)
                                            lineTo(w * 0.92f, h * 0.72f)
                                            quadraticTo(w * 0.5f, h * 0.98f, w * 0.08f, h * 0.72f)
                                            lineTo(0f, h * 0.22f)
                                            close()
                                        }
                                        drawPath(
                                            path = path,
                                            color = primaryColor,
                                            style = Stroke(width = 1.dp.toPx())
                                        )
                                    }
                                }

                                Text(
                                    text = "$dayNum",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isActive || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) Color(0xFF424242) // Texto oscuro sobre fondo claro
                                            else if (isToday) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            if (currentDayIndex >= days.size) break
        }
    }
}

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
fun StylizedParticleFlame(
    modifier: Modifier = Modifier,
    intensity: Float = 0.5f // 0.0 to 1.0
) {
    // 1. Smooth intensity to avoid sudden jumps in size/color
    val animatedIntensity by animateFloatAsState(
        targetValue = intensity.coerceIn(0f, 1f),
        animationSpec = tween(1200),
        label = "flame_intensity"
    )

    // 2. Manual Phase for PERFECT continuity
    var phase by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        var lastTime = withFrameNanos { it }
        while (true) {
            withFrameNanos { time ->
                val dt = (time - lastTime) / 1_000_000_000f
                lastTime = time
                // Speed varies from 2.0 rad/s to 5.5 rad/s
                val speed = 2.0f + (3.5f * animatedIntensity)
                phase += dt * speed
            }
        }
    }

    // Scaling: Compact and ergonomic
    val scaleMultiplier = 0.8f + (0.3f * animatedIntensity)

    // Particles (simple transition is fine here)
    val infiniteTransition = rememberInfiniteTransition(label = "flame_particles")
    val particleCount = if (animatedIntensity > 0.7f) 10 else 5
    val particleProgress = List(particleCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500 + i * 150, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "p$i"
        )
    }

    val flameColors = getFlameColors(animatedIntensity)

    Canvas(modifier = modifier.scale(scaleMultiplier)) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val baseLineY = h * 0.95f

        // 3. INTERNAL PHYSICS: "Sway" and "Lick" effect
        // The top of the flame sways more than the base
        fun drawFlameTongue(widthScale: Float, heightScale: Float, color: Color, xOffset: Float, pShift: Float = 0f) {
            val fw = w * widthScale
            val currentFh = h * (heightScale + sin(phase * 1.1f + pShift) * 0.05f * animatedIntensity)
            
            // Sway calculation: the air makes the flame lick side to side
            val lateralSway = sin(phase * 0.8f + pShift) * (w * 0.15f * animatedIntensity)
            val tipX = centerX + xOffset + lateralSway
            val tipY = baseLineY - currentFh

            val path = Path().apply {
                moveTo(tipX, tipY)
                // Right Curve (bending with sway)
                cubicTo(
                    centerX + xOffset + fw * 0.6f + lateralSway * 0.4f, baseLineY - currentFh * 0.4f,
                    centerX + xOffset + fw * 0.45f, baseLineY,
                    centerX + xOffset, baseLineY
                )
                // Left Curve (bending with sway)
                cubicTo(
                    centerX + xOffset - fw * 0.45f, baseLineY,
                    centerX + xOffset - fw * 0.6f + lateralSway * 0.4f, baseLineY - currentFh * 0.4f,
                    tipX, tipY
                )
            }
            drawPath(path = path, color = color)
        }

        // 4. SOFT GLOW: Pulse an aura behind the flame
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(flameColors.outer.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(centerX, baseLineY - h * 0.4f),
                radius = w * (0.6f + 0.2f * animatedIntensity)
            ),
            radius = w * (0.6f + 0.2f * animatedIntensity),
            center = Offset(centerX, baseLineY - h * 0.4f)
        )

        // --- Layers ---
        val gap = 3.dp.toPx() * (0.6f + 0.4f * animatedIntensity)

        // Layer 1: Outer
        drawFlameTongue(0.68f, 0.82f, flameColors.outer, -gap, 0f)
        drawFlameTongue(0.68f, 0.82f, flameColors.outer, gap, 1.3f)

        // Layer 2: Middle
        drawFlameTongue(0.48f, 0.62f, flameColors.mid, -gap * 0.5f, 0.7f)
        drawFlameTongue(0.48f, 0.62f, flameColors.mid, gap * 0.5f, 2.1f)

        // Layer 3: Inner Core
        drawFlameTongue(0.32f, 0.38f, flameColors.inner, 0f, 3.6f)

        // --- Particles (Sparks) following the sway ---
        particleProgress.forEachIndexed { index, progress ->
            val pSway = sin(phase * 0.6f + index) * (w * (0.15f + 0.2f * animatedIntensity))
            val pX = centerX + pSway
            val pY = baseLineY - (progress.value * h * (1.15f))
            val pAlpha = (1f - progress.value) * (0.4f + 0.6f * animatedIntensity)
            val pSize = (1f - progress.value) * (2.5f + 4f * animatedIntensity)

            if (pY < baseLineY - 5f) {
                drawCircle(
                    color = if (index % 2 == 0) flameColors.inner else flameColors.mid,
                    radius = pSize,
                    center = Offset(pX, pY),
                    alpha = pAlpha
                )
            }
        }
    }
}

private data class FlamePalette(val outer: Color, val mid: Color, val inner: Color)

private fun getFlameColors(intensity: Float): FlamePalette {
    return when {
        intensity < 0.3f -> {
            // Cool Phase: Red & Deep Orange
            val f = intensity / 0.3f
            FlamePalette(
                outer = lerpColor(Color(0xFF8B0000), Color(0xFFE25822), f),
                mid = lerpColor(Color(0xFFB22222), Color(0xFFD35400), f),
                inner = lerpColor(Color(0xFFE25822), Color(0xFFFF9800), f)
            )
        }
        intensity < 0.6f -> {
            // Warm Phase: Orange & Yellow
            val f = (intensity - 0.3f) / 0.3f
            FlamePalette(
                outer = lerpColor(Color(0xFFE25822), Color(0xFFFF7700), f),
                mid = lerpColor(Color(0xFFD35400), Color(0xFFFF9800), f),
                inner = lerpColor(Color(0xFFFF9800), Color(0xFFFFD726), f)
            )
        }
        intensity < 0.85f -> {
            // Hot Phase: Yellow & White
            val f = (intensity - 0.6f) / 0.25f
            FlamePalette(
                outer = lerpColor(Color(0xFFFF7700), Color(0xFFFFD726), f),
                mid = lerpColor(Color(0xFFFF9800), Color(0xFFFFFFFF), f),
                inner = lerpColor(Color(0xFFFFD726), Color(0xFFFFFFFF), f)
            )
        }
        else -> {
            // Super Intense: White & Blue (Electric Cyan/White)
            val f = (intensity - 0.85f) / 0.15f
            FlamePalette(
                outer = lerpColor(Color(0xFFFFD726), Color(0xFF006CF0), f),
                mid = lerpColor(Color(0xFFFFFFFF), Color(0xFF00E5FF), f),
                inner = lerpColor(Color(0xFFFFFFFF), Color(0xFFE0F7FA), f)
            )
        }
    }
}

private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

@Composable
fun StylizedConsistencyShield(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 1.0f, h * 0.22f)
            lineTo(w * 0.92f, h * 0.72f)
            quadraticTo(w * 0.5f, h * 0.98f, w * 0.08f, h * 0.72f)
            lineTo(0f, h * 0.22f)
            close()
        }

        // 1. Resplandor exterior sutil (Glow)
        drawPath(
            path = path,
            color = Color(0xFFFFD600).copy(alpha = 0.2f),
            style = Stroke(width = 4.dp.toPx())
        )

        // 2. Relleno con degradado para profundidad
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFEB3B), // Amarillo brillante arriba
                    Color(0xFFFBC02D)  // Amarillo oscuro/dorado abajo
                )
            )
        )

        // 3. Borde metálico (Oro Viejo)
        drawPath(
            path = path,
            color = Color(0xFFD4AF37), // Color dorado metálico
            style = Stroke(width = 1.5.dp.toPx())
        )
        
        // 4. Brillo interno (Highlight)
        val shinePath = Path().apply {
            moveTo(w * 0.2f, h * 0.25f)
            lineTo(w * 0.4f, h * 0.25f)
        }
        drawPath(
            path = shinePath,
            color = Color.White.copy(alpha = 0.4f),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
fun ConsistencyShieldBadge(streak: Int) {
    if (streak <= 0) return

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        StylizedConsistencyShield(modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$streak",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
