package com.breaswl.spexerutil.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.breaswl.spexerutil.ui.theme.ParticleCyan
import com.breaswl.spexerutil.ui.theme.ParticlePurple
import com.breaswl.spexerutil.ui.theme.ParticleWhite
import kotlin.random.Random

data class Particle(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val size: Float,
    val color: Color,
    val duration: Int
)

@Composable
fun BackgroundParticles(modifier: Modifier = Modifier) {
    val particles = remember {
        List(30) { index ->
            val random = Random(index)
            Particle(
                startX = random.nextFloat(),
                startY = random.nextFloat(),
                endX = random.nextFloat(),
                endY = random.nextFloat(),
                size = random.nextFloat() * 3f + 1f,
                color = when (random.nextInt(3)) {
                    0 -> ParticlePurple.copy(alpha = 0.3f)
                    1 -> ParticleCyan.copy(alpha = 0.4f)
                    else -> ParticleWhite.copy(alpha = 0.2f)
                },
                duration = random.nextInt(10000) + 15000
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animations = particles.map { particle ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_${particle.hashCode()}"
        )
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            val progress = animations[index].value
            val x = (particle.startX + (particle.endX - particle.startX) * progress) * size.width
            val y = (particle.startY + (particle.endY - particle.startY) * progress) * size.height
            
            drawCircle(
                color = particle.color,
                radius = particle.size.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

