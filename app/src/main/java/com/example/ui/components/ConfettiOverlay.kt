package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ConfettiParticle(
    val xRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val angle: Float
)

@Composable
fun ConfettiOverlay(
    isActive: Boolean,
    onAnimationEnd: () -> Unit
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(
            Color(0xFFFF8DA1),
            Color(0xFFFFD180),
            Color(0xFF80D8FF),
            Color(0xFFB388FF),
            Color(0xFFA7F3D0),
            Color(0xFFFF80AB)
        )
        List(65) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.8f,
                size = 12f + Random.nextFloat() * 14f,
                color = colors[Random.nextInt(colors.size)],
                angle = Random.nextFloat() * 360f
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
        onAnimationEnd()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val p = progress.value
        val canvasW = size.width
        val canvasH = size.height

        particles.forEach { particle ->
            val y = (p * canvasH * particle.speed * 1.2f) - 50f
            val x = (particle.xRatio * canvasW) + kotlin.math.sin((p * 10f + particle.angle).toDouble()).toFloat() * 30f
            val alpha = (1f - p).coerceIn(0f, 1f)

            drawRect(
                color = particle.color.copy(alpha = alpha),
                topLeft = Offset(x, y),
                size = Size(particle.size, particle.size * 0.7f)
            )
        }
    }
}
