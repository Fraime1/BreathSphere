package com.breaswl.spexerutil.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.breaswl.spexerutil.data.BreathingMode

enum class BreathingPhase {
    INHALE, HOLD, EXHALE, HOLD_AFTER_EXHALE
}

@Composable
fun BreathingSphere(
    isActive: Boolean,
    mode: BreathingMode,
    sphereColor1: Color,
    sphereColor2: Color,
    currentPhase: BreathingPhase,
    modifier: Modifier = Modifier
) {
    // Simple size and glow based on current phase
    val (size, glowIntensity) = when (currentPhase) {
        BreathingPhase.INHALE -> Pair(1f, 0.8f)
        BreathingPhase.HOLD -> Pair(1f, 0.8f)
        BreathingPhase.EXHALE -> Pair(0.5f, 0.3f)
        BreathingPhase.HOLD_AFTER_EXHALE -> Pair(0.5f, 0.3f)
    }
    
    // Animate between start and end values
    val animatedSize by animateFloatAsState(
        targetValue = if (isActive) size else 0.5f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "size"
    )
    
    val animatedGlow by animateFloatAsState(
        targetValue = if (isActive) glowIntensity else 0.3f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "glow"
    )
    
    Canvas(modifier = modifier.size(300.dp)) {
        val radius = this.size.minDimension / 2 * animatedSize
        val center = Offset(this.size.width / 2, this.size.height / 2)
        
        // Draw outer glow
        for (i in 3 downTo 1) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sphereColor1.copy(alpha = animatedGlow * 0.3f / i),
                        sphereColor2.copy(alpha = animatedGlow * 0.2f / i),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * (1 + i * 0.3f)
                ),
                radius = radius * (1 + i * 0.3f),
                center = center
            )
        }
        
        // Draw main sphere
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    sphereColor1.copy(alpha = 0.6f + animatedGlow * 0.4f),
                    sphereColor2.copy(alpha = 0.5f + animatedGlow * 0.3f),
                    sphereColor2.copy(alpha = 0.3f)
                ),
                center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
                radius = radius
            ),
            radius = radius,
            center = center
        )
        
        // Add highlight for bubble effect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.4f * animatedGlow),
                    Color.Transparent
                ),
                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
                radius = radius * 0.4f
            ),
            radius = radius * 0.4f,
            center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f)
        )
    }
}