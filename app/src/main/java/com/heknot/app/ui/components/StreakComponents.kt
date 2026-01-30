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
