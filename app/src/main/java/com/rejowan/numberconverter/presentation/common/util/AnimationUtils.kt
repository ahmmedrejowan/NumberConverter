package com.rejowan.numberconverter.presentation.common.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Adds a bounce animation when the composable is pressed.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    animationDuration: Int = 100
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = tween(animationDuration),
        label = "bounce_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(false)
                    isPressed = true
                    val up = waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

/**
 * Adds a press scale effect using interaction source.
 */
@Composable
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = tween(100),
        label = "press_scale"
    )

    return this.scale(scale)
}

/**
 * Standard animation durations
 */
object AnimationDurations {
    const val FAST = 150
    const val MEDIUM = 300
    const val SLOW = 500
}

/**
 * Standard enter/exit animation specs
 */
object AnimationSpecs {
    fun <T> fastTween() = tween<T>(AnimationDurations.FAST)
    fun <T> mediumTween() = tween<T>(AnimationDurations.MEDIUM)
    fun <T> slowTween() = tween<T>(AnimationDurations.SLOW)
}
